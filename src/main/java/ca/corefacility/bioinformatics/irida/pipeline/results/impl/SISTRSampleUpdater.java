package ca.corefacility.bioinformatics.irida.pipeline.results.impl;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Component
public class SISTRSampleUpdater implements AnalysisSampleUpdater {

	private static final Logger logger = LoggerFactory.getLogger(SISTRSampleUpdater.class);

	private static final String SISTR_FILE = "sistr-predictions";

	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;
	private Path outputFileDirectory;

	private static Map<String, String> SISTR_FIELDS = ImmutableMap
			.of("serovar", "SISTR serovar", "cgmlst_subspecies", "SISTR cgMLST Subspecies", "cgmlst_ST",
					"SISTR cgMLST Sequence Type", "qc_status", "SISTR QC Status");

	@Autowired
	public SISTRSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService,
			@Qualifier("outputFileBaseDirectory") Path outputFileDirectory) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
		this.outputFileDirectory = outputFileDirectory;
	}

	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) {
		AnalysisOutputFile sistrFile = analysis.getAnalysis().getAnalysisOutputFile(SISTR_FILE);

		//need to resolve the absolute path as it won't have been saved yet
		Path relativeFile = sistrFile.getFile();
		Path absoluteFile = outputFileDirectory.resolve(relativeFile);

		Map<String, MetadataEntry> stringEntries = new HashMap<>();
		try {
			String jsonFile = new Scanner(new BufferedReader(new FileReader(absoluteFile.toFile()))).useDelimiter("\\Z")
					.next();

			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> sistrResults = mapper
					.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {
					});

			if (sistrResults.size() > 0) {
				Map<String, Object> result = sistrResults.get(0);

				SISTR_FIELDS.entrySet().forEach(e -> {
					String value = result.get(e.getKey()).toString();
					PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(value, "text",
							analysis);
					stringEntries.put(e.getValue(), metadataEntry);
				});

				Map<MetadataTemplateField, MetadataEntry> metadataMap = metadataTemplateService
						.getMetadataMap(stringEntries);

				samples.forEach(s -> sampleService.updateFields(s.getId(), ImmutableMap.of("metadata", metadataMap)));

			} else {
				logger.error("SISTR results for file are not correctly formatted");
			}

		} catch (FileNotFoundException e) {
			logger.error("Couldn't open SISTR ouptut file", e);
		} catch (IOException e) {
			logger.error("Error parsing JSON from SISTR results", e);
		}
	}

	@Override
	public AnalysisType getAnalysisType() {
		return AnalysisType.SISTR_TYPING;
	}
}

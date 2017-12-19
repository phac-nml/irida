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
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Component
public class SISTRSampleUpdater implements AnalysisSampleUpdater {

	private static final Logger logger = LoggerFactory.getLogger(SISTRSampleUpdater.class);

	private static final String SISTR_FILE = "sistr-predictions";

	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;

	@Autowired
	public SISTRSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
	}

	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) {
		AnalysisOutputFile sistrFile = analysis.getAnalysis().getAnalysisOutputFile(SISTR_FILE);

		Map<String, MetadataEntry> stringEntries = new HashMap<>();
		try {
			String jsonFile = new Scanner(new BufferedReader(new FileReader(sistrFile.getFile().toFile())))
					.useDelimiter("\\Z").next();

			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> sistrResults = mapper
					.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {
					});

			if (sistrResults.size() > 0) {
				Map<String, Object> result = sistrResults.get(0);

				//serovar
				String serovarString = (String) result.get("serovar");
				PipelineProvidedMetadataEntry serovar = new PipelineProvidedMetadataEntry(serovarString, "text",
						analysis);
				stringEntries.put("SISTR_serovar", serovar);

				//serovar_antigen
				String antigenString = (String) result.get("serovar_antigen");
				PipelineProvidedMetadataEntry antigen = new PipelineProvidedMetadataEntry(antigenString, "text",
						analysis);
				stringEntries.put("SISTR_serovar_antigen", antigen);

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

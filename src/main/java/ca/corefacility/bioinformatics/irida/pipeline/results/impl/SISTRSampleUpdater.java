package ca.corefacility.bioinformatics.irida.pipeline.results.impl;

import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
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

/**
 * {@link AnalysisSampleUpdater} that adds a number of results from a SISTR run to the metadata of a {@link Sample}
 */
@Component
public class SISTRSampleUpdater implements AnalysisSampleUpdater {

	private static final Logger logger = LoggerFactory.getLogger(SISTRSampleUpdater.class);

	private static final String SISTR_FILE = "sistr-predictions";

	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;

	// @formatter:off
	private static Map<String, String> SISTR_FIELDS = ImmutableMap.of(
		"serovar", "SISTR serovar",
		"cgmlst_subspecies", "SISTR cgMLST Subspecies",
		"cgmlst_ST",
		"SISTR cgMLST Sequence Type",
		"qc_status", "SISTR QC Status"
	);
	// @formatter:on

	@Autowired
	public SISTRSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
	}

	/**
	 * Add SISTR results to the metadata of the given {@link Sample}s
	 *
	 * @param samples  The samples to update.
	 * @param analysis the {@link AnalysisSubmission} to apply to the samples
	 * @throws PostProcessingException if the method cannot read the "sistr-predictions" output file
	 */
	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) throws PostProcessingException {
		AnalysisOutputFile sistrFile = analysis.getAnalysis().getAnalysisOutputFile(SISTR_FILE);

		Path filePath = sistrFile.getFile();

		Map<String, MetadataEntry> stringEntries = new HashMap<>();
		try {
			//Read the JSON file from SISTR output
			String jsonFile = new Scanner(new BufferedReader(new FileReader(filePath.toFile()))).useDelimiter("\\Z")
					.next();

			// map the results into a Map
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> sistrResults = mapper
					.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {
					});

			if (sistrResults.size() > 0) {
				Map<String, Object> result = sistrResults.get(0);

				//loop through each of the requested fields and save the entries
				SISTR_FIELDS.entrySet().forEach(e -> {
					if (result.containsKey(e.getKey())) {
						String value = result.get(e.getKey()).toString();
						PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(value, "text",
								analysis);
						stringEntries.put(e.getValue(), metadataEntry);
					}
				});

				// convert string map into metadata fields
				Map<MetadataTemplateField, MetadataEntry> metadataMap = metadataTemplateService
						.getMetadataMap(stringEntries);

				//save metadata back to sample
				samples.forEach(s -> sampleService.updateFields(s.getId(), ImmutableMap.of("metadata", metadataMap)));

			} else {
				throw new PostProcessingException("SISTR results for file are not correctly formatted");
			}

		} catch (IOException e) {
			throw new PostProcessingException("Error parsing JSON from SISTR results", e);
		}
	}

	@Override
	public AnalysisType getAnalysisType() {
		return AnalysisType.SISTR_TYPING;
	}
}

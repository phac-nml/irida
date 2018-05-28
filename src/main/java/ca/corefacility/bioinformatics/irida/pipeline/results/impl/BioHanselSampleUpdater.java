package ca.corefacility.bioinformatics.irida.pipeline.results.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

/**
 * {@link AnalysisSampleUpdater} for bio_hansel results to be written to metadata of {@link Sample}s.
 */
@Component
public class BioHanselSampleUpdater implements AnalysisSampleUpdater {
	private static final String BIO_HANSEL_RESULTS_FILE = "bio_hansel-results.json";
	private static final String SCHEME_KEY = "scheme";
	private static final String VERSION_KEY = "scheme_version";
	private static Map<String, String> BIO_HANSEL_RESULTS_FIELDS = ImmutableMap.of("subtype",
			"bio_hansel/%1$s/v%2$s/Subtype", "avg_tile_coverage", "bio_hansel/%1$s/v%2$s/Average Tile Coverage",
			"qc_status", "bio_hansel/%1$s/v%2$s/QC Status", "qc_message", "bio_hansel/%1$s/v%2$s/QC Message");
	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;

	@Autowired
	public BioHanselSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
	}

	private String formatField(String fieldFormat, String scheme, String version) {
		return String.format(fieldFormat, scheme, version);
	}

	/**
	 * Add bio_hansel results to the metadata of the given {@link Sample}s.
	 *
	 * @param samples  The samples to update.
	 * @param analysis The {@link AnalysisSubmission} to use for updating.
	 * @throws PostProcessingException if the updater could not complete its processing
	 */
	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) throws PostProcessingException {
		AnalysisOutputFile aof = analysis.getAnalysis()
				.getAnalysisOutputFile(BIO_HANSEL_RESULTS_FILE);

		Path filePath = aof.getFile();

		Map<String, MetadataEntry> stringEntries = new HashMap<>();
		try {
			//Read the JSON file from SISTR output
			@SuppressWarnings("resource") String jsonText = new Scanner(
					new BufferedReader(new FileReader(filePath.toFile()))).useDelimiter("\\Z")
					.next();

			// map the results into a Map
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> maps = mapper.readValue(jsonText, new TypeReference<List<Map<String, Object>>>() {
			});

			if (maps.size() > 0) {
				Map<String, Object> result = maps.get(0);

				final String scheme = (String) result.get(SCHEME_KEY);
				final String version = (String) result.get(VERSION_KEY);

				//loop through each of the requested fields and save the entries
				BIO_HANSEL_RESULTS_FIELDS.forEach((key, fieldFmt) -> {
					if (result.containsKey(key) && result.get(key) != null) {
						String value = result.get(key)
								.toString();
						PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(value, "text",
								analysis);
						stringEntries.put(formatField(fieldFmt, scheme, version), metadataEntry);
					}
				});

				// convert string map into metadata fields
				Map<MetadataTemplateField, MetadataEntry> metadataMap = metadataTemplateService.getMetadataMap(
						stringEntries);

				//save metadata back to sample
				samples.forEach(s -> {
					s.mergeMetadata(metadataMap);
					sampleService.updateFields(s.getId(), ImmutableMap.of("metadata", s.getMetadata()));
				});

			} else {
				throw new PostProcessingException(filePath + " not correctly formatted. Expected valid JSON.");
			}

		} catch (IOException e) {
			throw new PostProcessingException("Error parsing JSON from " + filePath, e);
		}
	}

	/**
	 * Gets the {@link AnalysisType} this updater service handles.
	 *
	 * @return The {@link AnalysisType}.
	 */
	@Override
	public AnalysisType getAnalysisType() {
		return AnalysisType.BIO_HANSEL;
	}
}

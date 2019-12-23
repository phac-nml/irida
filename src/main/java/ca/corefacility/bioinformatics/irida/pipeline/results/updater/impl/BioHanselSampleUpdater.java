package ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
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
	private static final Logger logger = LoggerFactory.getLogger(BioHanselSampleUpdater.class);
	private static final String BIO_HANSEL_RESULTS_FILE = "bio_hansel-results.json";
	private static final String SCHEME_KEY = "scheme";
	private static final String VERSION_KEY = "scheme_version";
	private static final String TMPL_NAME_FMT = "bio_hansel/%1$s/v%2$s";
	// @formatter:off
	private static Map<String, String> BIO_HANSEL_RESULTS_FIELDS = ImmutableMap.of(
			"subtype", "Subtype",
			"qc_status", "QC Status",
			"qc_message", "QC Message",
			"avg_tile_coverage", "Average Tile Coverage"
	);
	// @formatter:on
	private MetadataTemplateService metadataTemplateService;
	private SampleService sampleService;

	@Autowired
	public BioHanselSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
	}

	/**
	 * Add bio_hansel results to the metadata of the given {@link Sample}.
	 * <p>
	 * Create a bio_hansel {@link MetadataTemplate} for each {@link Project} of the {@link Sample} if one doesn't exist.
	 *
	 * @param samples  The sample to update (collection should only have one {@link Sample} object).
	 * @param analysis Use the results from this {@link AnalysisSubmission} to update the {@link Sample} metadata.
	 * @throws PostProcessingException if the updater could not complete its processing
	 */
	@Override
	public void update(Collection<Sample> samples, AnalysisSubmission analysis) throws PostProcessingException {
		if (samples.size() != 1) {
			throw new PostProcessingException(
					"Expected one sample; got '" + samples.size() + "' for analysis [id=" + analysis.getId() + "]");
		}
		final Sample sample = samples.iterator()
				.next();

		AnalysisOutputFile aof = analysis.getAnalysis()
				.getAnalysisOutputFile(BIO_HANSEL_RESULTS_FILE);

		Path filePath = aof.getFile();

		Map<String, MetadataEntry> stringEntries = new HashMap<>();
		try {
			@SuppressWarnings("resource") String jsonText = new Scanner(
					new BufferedReader(new FileReader(filePath.toFile()))).useDelimiter("\\Z")
					.next();
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> maps = mapper.readValue(jsonText, new TypeReference<List<Map<String, Object>>>() {
			});
			if (maps.size() > 0) {
				Map<String, Object> result = maps.get(0);

				final String scheme = (String) result.get(SCHEME_KEY);
				final String version = (String) result.get(VERSION_KEY);
				final String baseNamespace = getBaseNamespace(scheme, version);
				BIO_HANSEL_RESULTS_FIELDS.forEach((key, field) -> {
					final String formattedField = getNamespacedField(baseNamespace, field);
					if (result.containsKey(key)) {
						String value = result.get(key)
								.toString();
						PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(value, "text",
								analysis);
						stringEntries.put(formattedField, metadataEntry);
					} else {
						logger.warn("bio_hansel output file '" + filePath.toFile()
								.getAbsolutePath() + "' does not contain expected key '" + key
								+ "'. Please check the format of this file!");
					}
				});

				Set<MetadataEntry> metadataSet = metadataTemplateService.getMetadataSet(stringEntries);

				sample.mergeMetadata(metadataSet);
				sampleService.updateFields(sample.getId(),
						ImmutableMap.of("metadataEntries", sample.getMetadataEntries()));
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
		return BuiltInAnalysisTypes.BIO_HANSEL;
	}

	/**
	 * Get the base bio_hansel metadata field namespace.
	 * <p>
	 * For example, `bio_hansel/heidelberg/v0.5.0`, so that metadata fields from different analyses of bio_hansel will
	 * not clash, e.g. `bio_hansel/heidelberg/v0.5.0/Subtype` vs `bio_hansel/enteritidis/v0.7.0/Subtype`.
	 *
	 * @param scheme bio_hansel scheme name.
	 * @param version bio_hansel scheme version.
	 * @return Base bio_hansel metadata field namespace prefix.
	 */
	private String getBaseNamespace(String scheme, String version) {
		return String.format(TMPL_NAME_FMT, scheme, version);
	}

	/**
	 * Given a base bio_hansel metadata field namespace, get the namespaced metadata field name.
	 *
	 * @param baseNamespace The base bio_hansel metadata field namespace, e.g. `bio_hansel/enteritidis/v0.7.0`
	 * @param field Metadata field, e.g. `Subtype`.
	 * @return Namespaced metadata field, e.g. `bio_hansel/enteritidis/v0.7.0/Subtype`.
	 */
	private String getNamespacedField(String baseNamespace, String field) {
		return baseNamespace + "/" + field;
	}
}

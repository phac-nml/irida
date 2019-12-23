package ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * {@link AnalysisSampleUpdater} that adds a number of results from a SISTR run to the metadata of a {@link Sample}
 */
@Component
public class SISTRSampleUpdater implements AnalysisSampleUpdater {
	private static final String SISTR_FILE = "sistr-predictions";

	private MetadataTemplateService metadataTemplateService;
	private IridaWorkflowsService iridaWorkflowsService;
	private SampleService sampleService;

	// @formatter:off
	private static Map<String, String> SISTR_FIELDS = ImmutableMap.<String,String>builder()
		.put("serovar", "SISTR serovar")
		.put("cgmlst_subspecies", "SISTR cgMLST Subspecies")
		.put("cgmlst_ST", "SISTR cgMLST Sequence Type")
		.put("qc_status", "SISTR QC Status")
		.put("serogroup", "SISTR Serogroup")
		.put("o_antigen", "SISTR O-antigen")
		.put("h1", "SISTR H1")
		.put("h2", "SISTR H2")
		.put("cgmlst_matching_alleles", "SISTR cgMLST Alleles Matching Genome")
		.build();
	// @formatter:on

	@Autowired
	public SISTRSampleUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService,
							  IridaWorkflowsService iridaWorkflowsService) {
		this.metadataTemplateService = metadataTemplateService;
		this.sampleService = sampleService;
		this.iridaWorkflowsService = iridaWorkflowsService;
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
			IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysis.getWorkflowId());
			String workflowVersion = iridaWorkflow.getWorkflowDescription().getVersion();

			//Read the JSON file from SISTR output
			@SuppressWarnings("resource")
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
						Object valueObject = result.get(e.getKey());
						String value = (valueObject != null ? valueObject.toString() : "");
						PipelineProvidedMetadataEntry metadataEntry = new PipelineProvidedMetadataEntry(value, "text",
								analysis);
						stringEntries.put(e.getValue() + " (v"+workflowVersion+")", metadataEntry);
					}
				});

				// convert string map into metadata fields
				Set<MetadataEntry> metadataSet = metadataTemplateService.getMetadataSet(stringEntries);

				//save metadata back to sample
				samples.forEach(s -> {
					s.mergeMetadata(metadataSet);
					sampleService.updateFields(s.getId(),
							ImmutableMap.of("metadataEntries", s.getMetadataEntries()));
				});

			} else {
				throw new PostProcessingException("SISTR results for file are not correctly formatted");
			}

		} catch (IOException e) {
			throw new PostProcessingException("Error parsing JSON from SISTR results", e);
		} catch (IridaWorkflowNotFoundException e) {
			throw new PostProcessingException("Workflow is not found", e);
		}
	}

	@Override
	public AnalysisType getAnalysisType() {
		return BuiltInAnalysisTypes.SISTR_TYPING;
	}
}

package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleMetadataResponse;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

import io.swagger.v3.oas.annotations.Operation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller to handle storing and retrieving metadata from a {@link Sample}
 */
@Controller
public class RESTSampleMetadataController {
	private static final Logger logger = LoggerFactory.getLogger(RESTSampleMetadataController.class);

	//rel for getting an individual sample's metadata
	public static final String METADATA_REL = "sample/metadata";

	//rel for getting all metadata for a project
	public static final String ALL_METADATA_REL = "project/samples/metadata";

	public static final String SAMPLE_REL = "sample";

	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private ProjectService projectService;

	@Autowired
	public RESTSampleMetadataController(SampleService sampleService, MetadataTemplateService metadataTemplateService,
			ProjectService projectService) {
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.projectService = projectService;
	}

	/**
	 * Get the metadata for a given {@link Sample}
	 *
	 * @param sampleId the id of the {@link Sample} to get metadata for
	 * @return the metadata for the sample
	 */
	@Operation(operationId = "getSampleMetadata", summary = "Find the metadata for a given sample",
			description = "Get the metadata for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.GET)
	public ResponseResource<SampleMetadataResponse> getSampleMetadata(@PathVariable Long sampleId) {
		logger.trace("Getting sample metadata for " + sampleId);
		Sample s = sampleService.read(sampleId);

		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(s);

		SampleMetadataResponse response = buildSampleMetadataResponse(s, metadataForSample);

		ResponseResource<SampleMetadataResponse> responseObject = new ResponseResource<>(response);
		return responseObject;
	}

	/**
	 * Get all the sample metadata for a given {@link Project}
	 *
	 * @param projectId the id of the {@link Project} to get metadata for
	 * @return A collection of metadata for all the {@link Sample}s in the {@link Project}
	 */
	@Operation(operationId = "getProjectSampleMetadata",
			summary = "Find the metadata for all the samples of a given project",
			description = "Get all the sample metadata for a given project.", tags = "projects")
	@RequestMapping(value = "/api/projects/{projectId}/samples/metadata", method = RequestMethod.GET)
	public ResponseResource<ResourceCollection<SampleMetadataResponse>> getProjectSampleMetadata(
			final @PathVariable Long projectId) {
		ResourceCollection<SampleMetadataResponse> resources = new ResourceCollection<>();
		Integer MAX_PAGE_SIZE = 5000;

		//get the project and samples for the project
		Project project = projectService.read(projectId);
		Sort sort = Sort.by(Sort.Direction.ASC, "sample.id");

		List<MetadataTemplateField> metadataTemplateFields = metadataTemplateService
				.getPermittedFieldsForCurrentUser(project, true);

		//fetch MAX_PAGE_SIZE samples at a time for the project
		Page<ProjectSampleJoin> page = sampleService.getFilteredSamplesForProjects(Arrays.asList(project),
				Collections.emptyList(), "", "", "", null, null, 0, MAX_PAGE_SIZE, sort);
		while (!page.isEmpty()) {
			List<Sample> samples = page.stream().map(ProjectSampleJoin::getObject).collect(Collectors.toList());
			List<Long> sampleIds = samples.stream().map(Sample::getId).collect(Collectors.toList());
			ProjectMetadataResponse metadataResponse = sampleService.getMetadataForProjectSamples(project, sampleIds,
					metadataTemplateFields);

			Map<Long, Set<MetadataEntry>> metadataForProject = metadataResponse.getMetadata();

			//for each sample
			for (Sample s : samples) {
				//get the metadata for that sample
				Set<MetadataEntry> metadataForSample = metadataForProject.get(s.getId());

				//build the response
				SampleMetadataResponse response = buildSampleMetadataResponse(s, metadataForSample);
				resources.add(response);
			}

			// Get the next page
			page = sampleService.getFilteredSamplesForProjects(Arrays.asList(project), Collections.emptyList(), "", "",
					"", null, null, page.getNumber() + 1, MAX_PAGE_SIZE, sort);
		}

		//add a link back to this collection
		resources.add(
				linkTo(methodOn(RESTSampleMetadataController.class).getProjectSampleMetadata(projectId)).withSelfRel());

		ResponseResource<ResourceCollection<SampleMetadataResponse>> responseObject = new ResponseResource<>(resources);
		return responseObject;
	}

	/**
	 * Save new metadata for a {@link Sample}. Note this will overwrite the existing metadata
	 *
	 * @param sampleId    the id of the {@link Sample} to save new metadata
	 * @param metadataMap the metadata to save to the {@link Sample}
	 * @return the updated {@link Sample}
	 */
	@Operation(operationId = "saveSampleMetadata", summary = "Save the metadata for a given sample",
			description = "Save the metadata for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.POST)
	public ResponseResource<SampleMetadataResponse> saveSampleMetadata(@PathVariable Long sampleId,
			@RequestBody Map<String, MetadataEntry> metadataMap) {
		Sample s = sampleService.read(sampleId);

		Set<MetadataEntry> metadata = metadataTemplateService.convertMetadataStringsToSet(metadataMap);

		sampleService.updateSampleMetadata(s, metadata);

		return getSampleMetadata(sampleId);
	}

	/**
	 * Add select new metadata fields to the {@link Sample}. Note this will only overwrite duplicate terms. Existing
	 * metadata will not be affected.
	 *
	 * @param sampleId    the {@link Sample} to add metadata to
	 * @param metadataMap the new metadata
	 * @return the updated {@link Sample}
	 */
	@Operation(operationId = "addSampleMetadata", summary = "Add new metadata fields for a given sample",
			description = "Add new metadata fields for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.PUT)
	public ResponseResource<SampleMetadataResponse> addSampleMetadata(@PathVariable Long sampleId,
			@RequestBody Map<String, MetadataEntry> metadataMap) {
		Sample s = sampleService.read(sampleId);

		Set<MetadataEntry> metadata = metadataTemplateService.convertMetadataStringsToSet(metadataMap);

		sampleService.mergeSampleMetadata(s, metadata);

		return getSampleMetadata(sampleId);
	}

	/**
	 * Build a {@link SampleMetadataResponse} object
	 *
	 * @param s the {@link Sample} to build the object from
	 * @return a constructed {@link SampleMetadataResponse}
	 */
	private SampleMetadataResponse buildSampleMetadataResponse(final Sample s, Set<MetadataEntry> metadataEntries) {
		SampleMetadataResponse response = new SampleMetadataResponse(metadataEntries);
		response.add(linkTo(methodOn(RESTSampleMetadataController.class).getSampleMetadata(s.getId())).withSelfRel());
		response.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(s.getId())).withRel(SAMPLE_REL));
		return response;
	}
}

package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleMetadataResponse;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * REST controller to handle storing and retrieving metadata from a
 * {@link Sample}
 */
@Controller
public class RESTSampleMetadataController {
	private static final Logger logger = LoggerFactory.getLogger(RESTSampleMetadataController.class);

	//rel for getting an indiviual sample's metadata
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
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.GET)
	public ModelMap getSampleMetadata(@PathVariable Long sampleId) {
		logger.trace("Getting sample metadata for " + sampleId);
		ModelMap modelMap = new ModelMap();
		Sample s = sampleService.read(sampleId);

		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(s);

		SampleMetadataResponse response = buildSampleMetadataResponse(s, metadataForSample);

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, response);
		return modelMap;
	}

	/**
	 * Get all the sample metadata for a given {@link Project}
	 *
	 * @param projectId the id of the {@link Project} to get metadata for
	 * @return A collection of metadata for all the {@link Sample}s in the {@link Project}
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/metadata")
	public ModelMap getProjectSampleMetadata(final @PathVariable Long projectId) {
		ModelMap modelMap = new ModelMap();

		ResourceCollection<SampleMetadataResponse> resources = new ResourceCollection<>();

		//get the project and samples for the project
		Project project = projectService.read(projectId);

		List<Sample> samples = sampleService.getSamplesForProjectShallow(project);
		Map<Long, Set<MetadataEntry>> metadataForProject = sampleService.getMetadataForProject(project);

		//for each sample
		for (Sample s : samples) {
			//get the metadata for that sample
			Set<MetadataEntry> metadataForSample = metadataForProject.get(s.getId());

			//if we dont' have any metadata, return an empty collection
			if (metadataForSample == null) {
				metadataForSample = new HashSet<>();
			}

			//build the response
			SampleMetadataResponse response = buildSampleMetadataResponse(s, metadataForSample);
			resources.add(response);
		}

		//add a link back to this collection
		resources.add(
				linkTo(methodOn(RESTSampleMetadataController.class).getProjectSampleMetadata(projectId)).withSelfRel());

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resources);

		return modelMap;
	}

	/**
	 * Save new metadata for a {@link Sample}. Note this will overwrite the
	 * existing metadata
	 *
	 * @param sampleId    the id of the {@link Sample} to save new metadata
	 * @param metadataMap the metadata to save to the {@link Sample}
	 * @return the updated {@link Sample}
	 */
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.POST)
	public ModelMap saveSampleMetadata(@PathVariable Long sampleId,
			@RequestBody Map<String, MetadataEntry> metadataMap) {
		Sample s = sampleService.read(sampleId);

		Set<MetadataEntry> metadata = metadataTemplateService.convertMetadataStringsToSet(metadataMap);

		sampleService.updateSampleMetadata(s, metadata);

		return getSampleMetadata(sampleId);
	}

	/**
	 * Add select new metadata fields to the {@link Sample}. Note this will only
	 * overwrite duplicate terms. Existing metadata will not be affected.
	 *
	 * @param sampleId    the {@link Sample} to add metadata to
	 * @param metadataMap the new metadata
	 * @return the updated {@link Sample}
	 */
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.PUT)
	public ModelMap addSampleMetadata(@PathVariable Long sampleId,
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

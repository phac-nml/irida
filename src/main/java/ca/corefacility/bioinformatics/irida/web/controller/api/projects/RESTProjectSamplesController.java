package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

import com.google.common.net.HttpHeaders;

/**
 * Controller for managing relationships between {@link Project} and
 * {@link Sample}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Controller
public class RESTProjectSamplesController {
	/**
	 * Rel to get to the project that this sample belongs to.
	 */
	public static final String REL_PROJECT = "sample/project";
	/**
	 * rel used for accessing the list of samples associated with a project.
	 */
	public static final String REL_PROJECT_SAMPLES = "project/samples";
	/**
	 * Reference to {@link ProjectService}.
	 */
	private ProjectService projectService;
	/**
	 * Reference to {@link SampleService}.
	 */
	private SampleService sampleService;

	/**
	 * Reference to {@link SequenceFileService}
	 */
	private SequenceFileService sequenceFileService;

	protected RESTProjectSamplesController() {
	}

	@Autowired
	public RESTProjectSamplesController(ProjectService projectService, SampleService sampleService,
			SequenceFileService sequenceFileService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
	}

	/**
	 * Copy an existing sample to a project.
	 * 
	 * @param projectId
	 *            the project to copy the sample to.
	 * @param sampleIds
	 *            the collection of sample IDs to copy.
	 * @return the response indicating that the sample was joined to the
	 *         project.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples", method = RequestMethod.POST, consumes = "application/idcollection+json")
	public ModelMap copySampleToProject(final @PathVariable Long projectId,
			final @RequestBody List<Long> sampleIds, HttpServletResponse response) {
		ModelMap modelMap = new ModelMap();
		Project p = projectService.read(projectId);
		ResourceCollection<LabelledRelationshipResource<Project,Sample>> labeledProjectSampleResources = new ResourceCollection
				<>(sampleIds.size());
		for (final long sampleId : sampleIds) {
			Sample sample = sampleService.read(sampleId);
			Join<Project, Sample> r = projectService.addSampleToProject(p, sample);
			LabelledRelationshipResource<Project, Sample> resource = new LabelledRelationshipResource
					<Project, Sample>(r.getLabel(),r);
			//add a labeled relationship resource to the resource collection that will fill the body of the response.
			resource.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sample.getId()))
					.withSelfRel());
			resource.add(linkTo(
					methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sample.getId()))
					.withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
			resource.add(linkTo(RESTProjectsController.class).slash(projectId).withRel(REL_PROJECT));
			labeledProjectSampleResources.add(resource);
			final String location = linkTo(
					methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withSelfRel()
					.getHref();
			response.addHeader(HttpHeaders.LOCATION, location);
		}
		//add a link to the project that was copied to.
		labeledProjectSampleResources
				.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSamples(projectId)).withSelfRel());
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, labeledProjectSampleResources);
		response.setStatus(HttpStatus.CREATED.value());
		
		return modelMap;
	}

	/**
	 * Create a new sample resource and create a relationship between the sample
	 * and the project.
	 *
	 * @param projectId
	 *            the identifier of the project that you want to add the sample
	 *            to.
	 * @param sample
	 *            the sample that you want to create.
	 * @return a response indicating that the sample was created and appropriate
	 *         location information.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples", method = RequestMethod.POST, consumes = "!application/idcollection+json")
	public ModelMap addSampleToProject(@PathVariable Long projectId, @RequestBody SampleResource sample, HttpServletResponse response) {
		ModelMap model = new ModelMap();
		
		// load the project that we're adding to
		Project p = projectService.read(projectId);

		// construct the sample that we're going to create
		Sample s = sample.getResource();

		// add the sample to the project
		Join<Project, Sample> r = projectService.addSampleToProject(p, s);

		// construct a link to the sample itself on the samples controller
		Long sampleId = r.getObject().getId();
		String location = linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId))
				.withSelfRel().getHref();
		
		// add a link to: 1) self, 2) sequenceFiles, 3) project
		sample.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withSelfRel());
		sample.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
		sample.add(linkTo(RESTProjectsController.class).slash(projectId).withRel(REL_PROJECT));
		
		// add the resource to the model
		model.addAttribute(RESTGenericController.RESOURCE_NAME,sample);
		
		//set the response status and add a location header
		response.setStatus(HttpStatus.CREATED.value());
		response.addHeader(HttpHeaders.LOCATION, location);
		
		return model;
	}
	

	/**
	 * Get the list of {@link Sample} associated with this {@link Project}.
	 *
	 * @param projectId
	 *            the identifier of the {@link Project} to get the
	 *            {@link Sample}s for.
	 * @return the list of {@link Sample}s associated with this {@link Project}.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples", method = RequestMethod.GET)
	public ModelMap getProjectSamples(@PathVariable Long projectId) {

		ModelMap modelMap = new ModelMap();
		Project p = projectService.read(projectId);
		List<Join<Project, Sample>> relationships = sampleService.getSamplesForProject(p);

		ResourceCollection<SampleResource> sampleResources = new ResourceCollection<>(relationships.size());

		for (Join<Project, Sample> r : relationships) {
			Sample sample = r.getObject();
			SampleResource sr = new SampleResource();
			sr.setResource(sample);
			sr.setSequenceFileCount(getSequenceFileCountForSampleResource(sr));
			sr.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sample.getId()))
					.withSelfRel());
			sr.add(linkTo(
					methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sample.getId()))
					.withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
			sr.add(linkTo(RESTProjectsController.class).slash(projectId).withRel(REL_PROJECT));
			sampleResources.add(sr);
		}

		sampleResources
				.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSamples(projectId)).withSelfRel());

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, sampleResources);

		return modelMap;
	}

	@RequestMapping(value = "/api/projects/{projectId}/samples/bySequencerId/{seqeuncerId}", method = RequestMethod.GET)
	public ModelAndView getProjectSampleBySequencerId(@PathVariable Long projectId, @PathVariable String seqeuncerId) {
		Project p = projectService.read(projectId);

		Sample sampleBySampleId = sampleService.getSampleBySequencerSampleId(p, seqeuncerId);

		SampleResource sr = new SampleResource();
		sr.setResource(sampleBySampleId);

		Link withSelfRel = linkTo(
				methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleBySampleId.getId()))
				.withSelfRel();
		String href = withSelfRel.getHref();

		RedirectView redirectView = new RedirectView(href);

		return new ModelAndView(redirectView);
	}

	/**
	 * Get the representation of a specific sample that's associated with the
	 * project.
	 *
	 * @param projectId
	 *            the {@link Project} identifier that the {@link Sample} should
	 *            be associated with.
	 * @param sampleId
	 *            the {@link Sample} identifier that we're looking for.
	 * @return a representation of the specific sample.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}", method = RequestMethod.GET)
	public ModelMap getProjectSample(@PathVariable Long projectId, @PathVariable Long sampleId) {
		ModelMap modelMap = new ModelMap();
		// load the project
		Project p = projectService.read(projectId);
		// get the sample for the project.
		Sample s = sampleService.getSampleForProject(p, sampleId);

		// prepare the sample for serializing to the client
		SampleResource sr = new SampleResource();
		sr.setResource(s);

		sr.setSequenceFileCount(getSequenceFileCountForSampleResource(sr));

		// add a link to: 1) self, 2) sequenceFiles, 3) project
		sr.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withSelfRel());
		sr.add(linkTo(RESTProjectsController.class).slash(projectId).withRel(REL_PROJECT));
		sr.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));

		// add the sample resource to the response
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, sr);

		return modelMap;
	}

	/**
	 * Remove a specific {@link Sample} from the collection of {@link Sample}s
	 * associated with a {@link Project}.
	 *
	 * @param projectId
	 *            the {@link Project} identifier.
	 * @param sampleId
	 *            the {@link Sample} identifier.
	 * @return a response including links back to the specific {@link Project}
	 *         and collection of {@link Sample}.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}", method = RequestMethod.DELETE)
	public ModelMap removeSampleFromProject(@PathVariable Long projectId, @PathVariable Long sampleId) {
		ModelMap modelMap = new ModelMap();

		// load the sample and project
		Project p = projectService.read(projectId);
		Sample s = sampleService.read(sampleId);

		// remove the relationship.
		projectService.removeSampleFromProject(p, s);

		// respond to the client.
		RootResource resource = new RootResource();
		// add links back to the collection of samples and to the project
		// itself.
		resource.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSamples(projectId)).withRel(
				REL_PROJECT_SAMPLES));
		resource.add(linkTo(RESTProjectsController.class).slash(projectId).withRel(RESTProjectsController.REL_PROJECT));

		// add the links to the response.
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resource);

		return modelMap;
	}

	/**
	 * Update a {@link Sample} details.
	 *
	 * @param projectId
	 *            the identifier of the {@link Project} that the {@link Sample}
	 *            belongs to.
	 * @param sampleId
	 *            the identifier of the {@link Sample}.
	 * @param updatedFields
	 *            the updated fields of the {@link Sample}.
	 * @return a response including links to the {@link Project} and
	 *         {@link Sample}.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}", method = RequestMethod.PATCH, consumes = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ModelMap updateSample(@PathVariable Long projectId, @PathVariable Long sampleId,
			@RequestBody Map<String, Object> updatedFields) {
		ModelMap modelMap = new ModelMap();

		// confirm that the project is related to the sample
		Project p = projectService.read(projectId);
		sampleService.getSampleForProject(p, sampleId);

		// issue an update request
		sampleService.update(sampleId, updatedFields);

		// respond to the client with a link to self, sequence files collection
		// and project.
		RootResource resource = new RootResource();
		resource.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId))
				.withSelfRel());
		resource.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(projectId, sampleId))
				.withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
		resource.add(linkTo(RESTProjectsController.class).slash(projectId).withRel(RESTProjectsController.REL_PROJECT));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resource);

		return modelMap;
	}

	/**
	 * Get the number of sequence files to a {@link SampleResource}
	 * 
	 * @param resource
	 *            The {@link SampleResource} to enhance
	 * @return The number of sequence files in the sample
	 */
	private int getSequenceFileCountForSampleResource(SampleResource resource) {
		List<Join<Sample, SequenceFile>> sequenceFilesForSample = sequenceFileService
				.getSequenceFilesForSample(resource.getResource());
		return sequenceFilesForSample.size();
	}
}

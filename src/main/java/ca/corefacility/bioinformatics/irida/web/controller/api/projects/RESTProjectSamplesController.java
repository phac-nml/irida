package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.ExistingSampleNameException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleAssemblyController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleMetadataController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

import com.google.common.net.HttpHeaders;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Controller for managing relationships between {@link Project} and
 * {@link Sample}.
 */
@Controller
public class RESTProjectSamplesController {

	private static final Logger logger = LoggerFactory.getLogger(RESTProjectSamplesController.class);

	/**
	 * Rel to get to the project that this sample belongs to.
	 */
	public static final String REL_PROJECT = "sample/project";
	/**
	 * rel used for accessing the list of samples associated with a project.
	 */
	public static final String REL_PROJECT_SAMPLES = "project/samples";

	public static final String REL_PROJECT_SAMPLE = "project/sample";

	/**
	 * Reference to {@link ProjectService}.
	 */
	private ProjectService projectService;
	/**
	 * Reference to {@link SampleService}.
	 */
	private SampleService sampleService;

	private MessageSource messageSource;

	protected RESTProjectSamplesController() {
	}

	@Autowired
	public RESTProjectSamplesController(ProjectService projectService, SampleService sampleService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.messageSource = messageSource;
	}

	/**
	 * Copy an existing sample to a project.
	 *
	 * @param projectId the project to copy the sample to.
	 * @param sampleIds the collection of sample IDs to copy.
	 * @param response  a reference to the servlet response.
	 * @return the response indicating that the sample was joined to the
	 * project.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples", method = RequestMethod.POST, consumes = "application/idcollection+json")
	public ModelMap copySampleToProject(final @PathVariable Long projectId, final @RequestBody List<Long> sampleIds,
			HttpServletResponse response, Locale locale) {

		ModelMap modelMap = new ModelMap();

		Project p = projectService.read(projectId);

		List<String> errors = new ArrayList<>();

		ResourceCollection<LabelledRelationshipResource<Project, Sample>> labeledProjectSampleResources = new ResourceCollection<>(
				sampleIds.size());
		for (final long sampleId : sampleIds) {
			Sample sample = sampleService.read(sampleId);
			Join<Project, Sample> join = null;
			try {
				join = projectService.addSampleToProject(p, sample, false);
			} catch (ExistingSampleNameException e) {
				logger.error(
						"Could not add sample to project because another sample exists with this name :" + e.getSample()
								.getSampleName());
				errors.add(messageSource.getMessage("rest.api.project.samples.warning.duplicate",
						new Object[] { sample.getId(), sample.getSampleName() }, locale));
			} catch (EntityExistsException e) {
				logger.warn("User tried to add a sample to a project where it already existed. project: " + projectId
						+ " sample: " + sampleId);
				errors.add(messageSource.getMessage("rest.api.project.samples.warning.exists",
						new Object[] { sample.getId(), sample.getSampleName() }, locale));

				join = sampleService.getSampleForProject(p, sampleId);
			}

			if (join != null) {
				LabelledRelationshipResource<Project, Sample> resource = new LabelledRelationshipResource<Project, Sample>(
						join.getLabel(), join);
				// add a labeled relationship resource to the resource collection
				// that will fill the body of the response.
				resource.add(
						linkTo(methodOn(RESTProjectSamplesController.class).getSample(sample.getId())).withSelfRel());
				resource.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId,
						sample.getId())).withRel(REL_PROJECT_SAMPLE));
				resource.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(
						sample.getId())).withRel(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
				resource.add(linkTo(RESTProjectsController.class).slash(projectId)
						.withRel(REL_PROJECT));
				labeledProjectSampleResources.add(resource);
				final String location = linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId,
						sampleId)).withSelfRel()
						.getHref();
				response.addHeader(HttpHeaders.LOCATION, location);
			}

		}
		// add a link to the project that was copied to.
		labeledProjectSampleResources.add(
				linkTo(methodOn(RESTProjectSamplesController.class).getProjectSamples(projectId)).withSelfRel());
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, labeledProjectSampleResources);

		if (!errors.isEmpty()) {
			modelMap.addAttribute("warnings", errors);
		}

		response.setStatus(HttpStatus.CREATED.value());

		return modelMap;
	}

	/**
	 * Create a new sample resource and create a relationship between the sample
	 * and the project.
	 *
	 * @param projectId the identifier of the project that you want to add the sample
	 *                  to.
	 * @param sample    the sample that you want to create.
	 * @param response  a reference to the servlet response.
	 * @return a response indicating that the sample was created and appropriate
	 * location information.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples", method = RequestMethod.POST, consumes = "!application/idcollection+json")
	public ModelMap addSampleToProject(@PathVariable Long projectId, @RequestBody Sample sample,
			HttpServletResponse response) {
		ModelMap model = new ModelMap();

		// load the project that we're adding to
		Project p = projectService.read(projectId);

		// add the sample to the project
		Join<Project, Sample> r = projectService.addSampleToProject(p, sample, true);

		// construct a link to the sample itself on the samples controller
		Long sampleId = r.getObject()
				.getId();
		String location = linkTo(methodOn(RESTProjectSamplesController.class).getSample(sampleId)).withSelfRel()
				.getHref();

		// add a link to: 1) self, 2) sequenceFiles, 3) project
		addLinksForSample(Optional.of(p), sample);

		// add the resource to the model
		model.addAttribute(RESTGenericController.RESOURCE_NAME, sample);

		// set the response status and add a location header
		response.setStatus(HttpStatus.CREATED.value());
		response.addHeader(HttpHeaders.LOCATION, location);

		return model;
	}

	/**
	 * Get the list of {@link Sample} associated with this {@link Project}.
	 *
	 * @param projectId the identifier of the {@link Project} to get the
	 *                  {@link Sample}s for.
	 * @return the list of {@link Sample}s associated with this {@link Project}.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples", method = RequestMethod.GET)
	public ModelMap getProjectSamples(@PathVariable Long projectId) {

		ModelMap modelMap = new ModelMap();
		Project p = projectService.read(projectId);
		List<Sample> samples = sampleService.getSamplesForProjectShallow(p);

		ResourceCollection<Sample> sampleResources = new ResourceCollection<>(samples.size());

		for (Sample sample : samples) {
			addLinksForSample(Optional.of(p), sample);
			sampleResources.add(sample);
		}

		sampleResources.add(
				linkTo(methodOn(RESTProjectSamplesController.class).getProjectSamples(projectId)).withSelfRel());

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, sampleResources);

		return modelMap;
	}

	/**
	 * Get samples by a given string name
	 *
	 * @param projectId   the Project to get samples from
	 * @param seqeuncerId the string id of the sample
	 * @return The found sample
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/bySequencerId/{seqeuncerId}", method = RequestMethod.GET)
	public ModelAndView getProjectSampleBySequencerId(@PathVariable Long projectId, @PathVariable String seqeuncerId) {
		Project p = projectService.read(projectId);

		Sample sampleBySampleId = sampleService.getSampleBySampleName(p, seqeuncerId);

		Link withSelfRel = linkTo(
				methodOn(RESTProjectSamplesController.class).getSample(sampleBySampleId.getId())).withSelfRel();
		String href = withSelfRel.getHref();

		RedirectView redirectView = new RedirectView(href);

		return new ModelAndView(redirectView);
	}

	/**
	 * Get the representation of a specific sample that's associated with the
	 * project.
	 *
	 * @param projectId the {@link Project} identifier that the {@link Sample} should
	 *                  be associated with.
	 * @param sampleId  the {@link Sample} identifier that we're looking for.
	 * @return a representation of the specific sample.
	 */
	@RequestMapping(value = "/api/projects/{projectId}/samples/{sampleId}", method = RequestMethod.GET)
	public ModelMap getProjectSample(@PathVariable Long projectId, @PathVariable Long sampleId) {
		// read project/sample to verify sample exists in project
		Project project = projectService.read(projectId);
		Sample s = sampleService.getSampleForProject(project, sampleId)
				.getObject();

		ModelMap modelMap = new ModelMap();

		addLinksForSample(Optional.of(project), s);

		// add a link to project
		s.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sampleId)).withRel(
				REL_PROJECT_SAMPLE));

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, s);

		return modelMap;
	}

	/**
	 * Read a {@link Sample} by its id
	 *
	 * @param sampleId the id of the {@link Sample} to read
	 * @return representation of the sample
	 */
	@RequestMapping(value = "/api/samples/{sampleId}", method = RequestMethod.GET)
	public ModelMap getSample(@PathVariable Long sampleId) {
		ModelMap modelMap = new ModelMap();
		Sample s = sampleService.read(sampleId);

		addLinksForSample(Optional.empty(), s);

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, s);
		return modelMap;
	}

	/**
	 * Add the required links for a sample individual: self, file pairs,
	 * unpaired files
	 *
	 * @param p optionally the project for this sample
	 * @param s The sample to add links to
	 */
	private void addLinksForSample(final Optional<Project> p, final Sample s) {
		s.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(s.getId())).withSelfRel());
		s.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).getSampleSequenceFiles(s.getId())).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES));
		s.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).listSequencingObjectsOfTypeForSample(s.getId(),
				RESTSampleSequenceFilesController.objectLabels.get(SequenceFilePair.class))).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_PAIRS));
		s.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).listSequencingObjectsOfTypeForSample(s.getId(),
				RESTSampleSequenceFilesController.objectLabels.get(SingleEndSequenceFile.class))).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_UNPAIRED));
		s.add(linkTo(methodOn(RESTSampleSequenceFilesController.class).listSequencingObjectsOfTypeForSample(s.getId(),
				RESTSampleSequenceFilesController.objectLabels.get(Fast5Object.class))).withRel(
				RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_FAST5));
		s.add(linkTo(methodOn(RESTSampleMetadataController.class).getSampleMetadata(s.getId())).withRel(
				RESTSampleMetadataController.METADATA_REL));
		s.add(linkTo(methodOn(RESTSampleAssemblyController.class).listAssembliesForSample(s.getId())).withRel(
				RESTSampleAssemblyController.REL_SAMPLE_ASSEMBLIES));

		if (p.isPresent()) {
			final Project project = p.get();
			s.add(linkTo(RESTProjectsController.class).slash(project.getId())
					.withRel(REL_PROJECT));
			s.add(linkTo(
					methodOn(RESTProjectSamplesController.class).getProjectSample(project.getId(), s.getId())).withRel(
					REL_PROJECT_SAMPLE));
		}
	}

	/**
	 * Remove a specific {@link Sample} from the collection of {@link Sample}s
	 * associated with a {@link Project}.
	 *
	 * @param projectId the {@link Project} identifier.
	 * @param sampleId  the {@link Sample} identifier.
	 * @return a response including links back to the specific {@link Project}
	 * and collection of {@link Sample}.
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
		resource.add(linkTo(RESTProjectsController.class).slash(projectId)
				.withRel(RESTProjectsController.REL_PROJECT));

		// add the links to the response.
		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, resource);

		return modelMap;
	}

	/**
	 * Update a {@link Sample} details.
	 *
	 * @param sampleId      the identifier of the {@link Sample}.
	 * @param updatedFields the updated fields of the {@link Sample}.
	 * @return a response including links to the {@link Project} and
	 * {@link Sample}.
	 */
	@RequestMapping(value = "/api/samples/{sampleId}", method = RequestMethod.PATCH, consumes = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ModelMap updateSample(@PathVariable Long sampleId, @RequestBody Map<String, Object> updatedFields) {
		ModelMap modelMap = new ModelMap();

		// issue an update request
		final Sample s = sampleService.updateFields(sampleId, updatedFields);
		addLinksForSample(Optional.empty(), s);

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, s);

		return modelMap;
	}

}

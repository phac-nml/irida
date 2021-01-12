package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.HashSet;

import ca.corefacility.bioinformatics.irida.service.remote.ProjectHashingService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ProjectHashResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for managing {@link Project}s in the database.
 *
 */
@Controller
@RequestMapping(value = "/api/projects")
public class RESTProjectsController extends RESTGenericController<Project> {

	private static final Logger logger = LoggerFactory.getLogger(RESTProjectsController.class);

	private ProjectService projectService;

	@Autowired
	ProjectHashingService projectHashingService;

	/**
	 * rel used for accessing an individual project.
	 */
	public static final String REL_PROJECT = "project";
	/**
	 * rel used for accessing users associated with a project.
	 */
	private static final String PROJECT_USERS_REL = "project/users";

	/**
	 * rel used for accessing analyses shared to a project.
	 */
	private static final String PROJECT_ANALYSES_REL = "project/analyses";

	/**
	 * rel used for the project status hash
	 */
	public static final String PROJECT_HASH_REL = "project/hash";

	/**
	 * Default constructor. Should not be used.
	 */
	protected RESTProjectsController() {
	}

	/**
	 * Constructor for {@link RESTProjectsController}, requires a reference to a
	 * {@link ProjectService}.
	 *
	 * @param projectService
	 *            the {@link ProjectService} to be used by this controller.
	 */
	@Autowired
	public RESTProjectsController(ProjectService projectService) {
		super(projectService, Project.class);
		this.projectService = projectService;
	}

	/**
	 * Get the deep project hash for the requested project
	 *
	 * @param projectId the ID of the project to read the hash for
	 * @return a modelmap containing the hash code
	 */
	@RequestMapping(value = "/{projectId}/hash", method = RequestMethod.GET)
	public ModelMap getProjectHash(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);

		Integer projectHash = projectHashingService.getProjectHash(project);

		ProjectHashResource projectHashResource = new ProjectHashResource(projectHash);

		ModelMap model = new ModelMap();
		model.addAttribute(RESOURCE_NAME, projectHashResource);

		projectHashResource.add(linkTo(methodOn(RESTProjectsController.class).getProjectHash(projectId)).withSelfRel());
		projectHashResource.add(linkTo(methodOn(RESTProjectsController.class).getResource(projectId)).withRel(REL_PROJECT));

		return model;
	}

	/**
	 * The {@link RESTProjectsController} should tell the client how to find the
	 * users for a specific {@link Project}.
	 *
	 * @param p
	 *            the {@link Project} to construct custom links for.
	 * @return a collection of custom links.
	 */
	@Override
	protected Collection<Link> constructCustomResourceLinks(Project p) {
		Collection<Link> links = new HashSet<>();
		Long projectId = p.getId();
		try {
			links.add(linkTo(methodOn(RESTProjectUsersController.class).getUsersForProject(p.getId()))
					.withRel(PROJECT_USERS_REL));
		} catch (ProjectWithoutOwnerException e) {
			logger.error("Got exception", e);
		}
		links.add(linkTo(methodOn(RESTProjectSamplesController.class).getProjectSamples(projectId))
				.withRel(RESTProjectSamplesController.REL_PROJECT_SAMPLES));
		links.add(linkTo(methodOn(RESTProjectAnalysisController.class).getProjectAnalyses(projectId))
				.withRel(PROJECT_ANALYSES_REL));
		links.add(linkTo(methodOn(RESTProjectsController.class).getProjectHash(projectId)).withRel(PROJECT_HASH_REL));
		return links;
	}
}

package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;

/**
 * Controller for managing {@link Project}s in the database.
 *
 */
@Controller
@RequestMapping(value = "/api/projects")
public class RESTProjectsController extends RESTGenericController<Project> {
	
	private static final Logger logger = LoggerFactory.getLogger(RESTProjectsController.class);

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
		return links;
	}
}

package ca.corefacility.bioinformatics.irida.web.controller.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;

import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller for managing users.
 */
@Controller
@RequestMapping(value = "/api/users")
public class RESTUsersController extends RESTGenericController<User> {

	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RESTUsersController.class);
	/**
	 * a rel for getting a handle on the projects that a user belongs to.
	 */
	public static final String REL_USER_PROJECTS = "user/projects";
	/**
	 * a map label for the projects associated with a user.
	 */
	public static final String USER_PROJECTS_MAP_LABEL = "projects";
	/**
	 * Reference to the {@link ProjectService}.
	 */
	private final ProjectService projectService;
	/**
	 * Reference to the {@link UserService}
	 */
	private final UserService userService;

	protected RESTUsersController() {
		this.projectService = null;
		this.userService = null;
	}

	/**
	 * Constructor, requires a reference to a {@link UserService} and a
	 * {@link ProjectService}.
	 *
	 * @param userService    the {@link UserService} that this controller uses.
	 * @param projectService the {@link ProjectService} that this controller uses.
	 */
	@Autowired
	public RESTUsersController(UserService userService, ProjectService projectService) {
		super(userService, User.class);
		this.userService = userService;
		this.projectService = projectService;
	}

	/**
	 * A collection of custom links for a specific {@link User}.
	 *
	 * @param u the {@link User} to create links for.
	 * @return the links for this {@link User}.
	 */
	@Override
	protected Collection<Link> constructCustomResourceLinks(User u) {
		Collection<Link> links = new HashSet<>();
		links.add(linkTo(RESTUsersController.class).slash(u.getUsername())
				.slash("projects")
				.withRel(REL_USER_PROJECTS));
		return links;
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "listAllUsers", summary = "Lists all users", description = "Lists all users.", tags = "users")
	@Override
	public ResponseResource<ResourceCollection<User>> listAllResources() {
		return super.listAllResources();
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "getUser", summary = "Find a user", description = "Get the user given the identifier.", tags = "users")
	@Override
	public ResponseResource<User> getResource(@PathVariable Long identifier) {
		return super.getResource(identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "createUser", summary = "Create a new user", description = "Create a new user.", tags = "users")
	@Override
	public ResponseResource<User> create(@RequestBody User resource, HttpServletResponse response) {
		return super.create(resource, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "deleteUser", summary = "Delete a user", description = "Delete a user given the identifier.", tags = "users")
	@Override
	public ResponseResource<RootResource> delete(@PathVariable Long identifier) {
		return super.delete(identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "updateUser", summary = "Update a user", description = "Update a user", tags = "users")
	@Override
	public ResponseResource<RootResource> update(@PathVariable Long identifier,
			@RequestBody Map<String, Object> representation) {
		return super.update(identifier, representation);
	}

	/**
	 * Get the collection of projects for a specific user.
	 *
	 * @param username the username for the desired user.
	 * @return a model containing the collection of projects for that user.
	 */
	@Operation(operationId = "getUserProjects", summary = "Find all the projects associated with a user", description = "Get the list of projects associated with a user.", tags = "users")
	@RequestMapping(value = "/{username}/projects", method = RequestMethod.GET)
	public ResponseProjectResource<ResourceCollection<Project>> getUserProjects(@PathVariable String username) {
		logger.debug("Loading projects for user [" + username + "]");

		// get the appropriate user from the database
		User u = userService.getUserByUsername(username);

		// get all of the projects that this user belongs to
		ResourceCollection<Project> resources = new ResourceCollection<>();
		List<Join<Project, User>> projects = projectService.getProjectsForUser(u);
		WebMvcLinkBuilder linkBuilder = linkTo(RESTProjectsController.class);

		// add the project and a self-rel link to the project representation
		for (Join<Project, User> join : projects) {
			Project project = join.getSubject();
			project.add(linkBuilder.slash(project.getId())
					.withSelfRel());
			resources.add(project);
		}

		ResponseProjectResource<ResourceCollection<Project>> responseObject = new ResponseProjectResource<>(resources);

		// respond to the user
		return responseObject;
	}

	/**
	 * Get the user account that is currently logged in. This is here so that a
	 * user interface client to the REST API can display more details about the
	 * current user than just username. This endpoint is *not* documented in the
	 * REST API.
	 *
	 * @return a representation of the currently logged in user.
	 */
	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public ResponseResource<User> getCurrentUser() {
		// get the current user from Spring Security.
		String username = SecurityContextHolder.getContext()
				.getAuthentication()
				.getName();
		logger.debug("Getting currently logged-in user: [" + username + "].");

		User u = userService.getUserByUsername(username);
		// get the user from the database.
		return getResource(u.getId());
	}

}

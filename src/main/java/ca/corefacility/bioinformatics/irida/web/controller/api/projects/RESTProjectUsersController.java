package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.*;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTUsersController;

import com.google.common.net.HttpHeaders;

/**
 * Controller for managing relationships between {@link Project} and {@link User}.
 */
@Controller
public class RESTProjectUsersController {

	/**
	 * key used in map when adding user to project.
	 */
	public static final String USER_ID_KEY = "userId";

	/**
	 * Key used in map for role when adding user to project
	 */
	public static final String USER_ROLE_KEY = "role";
	public static final String METADATA_ROLE_KEY = "metadataRole";

	/**
	 * Rel to get to the list of users associated with a project.
	 */
	public static final String REL_PROJECT_USERS = "project/users";
	/**
	 * Reference to {@link UserService} for managing {@link User}.
	 */
	private UserService userService;
	/**
	 * Reference to {@link ProjectService} for managing {@link Project}.
	 */
	private ProjectService projectService;

	protected RESTProjectUsersController() {
	}

	@Autowired
	public RESTProjectUsersController(UserService userService, ProjectService projectService) {
		this.userService = userService;
		this.projectService = projectService;
	}

	/**
	 * Get all users associated with a project.
	 *
	 * @param projectId the project id to get users for.
	 * @return a model with a collection of user resources.
	 * @throws ProjectWithoutOwnerException If removing a user will leave the project without an owner. Should NEVER be
	 *                                      thrown in this method, but needs to be listed.
	 */
	@Operation(operationId = "getProjectHash", summary = "Get all users for the given a project", description = "Get all users for the given a project.", tags = "projects")
	@RequestMapping(value = "/api/projects/{projectId}/users", method = RequestMethod.GET)
	public ResponseResource<ResourceCollection<User>> getUsersForProject(@PathVariable Long projectId)
			throws ProjectWithoutOwnerException {
		ResourceCollection<User> resources = new ResourceCollection<>();

		// get all of the users belonging to this project
		Project p = projectService.read(projectId);
		Collection<Join<Project, User>> relationships = userService.getUsersForProject(p);

		// for each of those relationships, retrieve the complete user object
		// and convert to a resource suitable for sending back to the client.
		for (Join<Project, User> r : relationships) {
			User u = r.getObject();
			u.add(linkTo(RESTUsersController.class).slash(u.getUsername())
					.withSelfRel());
			u.add(linkTo(methodOn(RESTProjectUsersController.class).removeUserFromProject(projectId,
					u.getUsername())).withRel(RESTGenericController.REL_RELATIONSHIP));

			resources.add(u);
		}

		// add a link to this resource to the response
		resources.add(linkTo(methodOn(RESTProjectUsersController.class, String.class).getUsersForProject(
				projectId)).withSelfRel());

		// prepare the response for the client
		ResponseResource<ResourceCollection<User>> responseObject = new ResponseResource<>(resources);

		return responseObject;
	}

	/**
	 * Add a relationship between a {@link Project} and a {@link User}.
	 *
	 * @param projectId      the project ID to add the user to.
	 * @param representation the JSON key-value pair that contains the identifier for the project and the identifier for
	 *                       the user.
	 * @param response       a reference to the servlet response.
	 * @return a response indicating that the collection was modified.
	 * @throws ProjectWithoutOwnerException this cannot actually be thrown, it's an artifact of using spring HATEOAS
	 *                                      {@code linkTo} and {@code methodOn}.
	 */
	@Operation(operationId = "addUserToProject", summary = "Add a user to the given project", description = "Add a user to the given project.", tags = "projects")
	@RequestMapping(value = "/api/projects/{projectId}/users", method = RequestMethod.POST)
	public ResponseResource<LabelledRelationshipResource<Project, User>> addUserToProject(@PathVariable Long projectId,
			@RequestBody Map<String, String> representation, HttpServletResponse response)
			throws ProjectWithoutOwnerException {
		// first, get the project
		Project p = projectService.read(projectId);
		String username = representation.get(USER_ID_KEY);
		// then, get the user
		User u = userService.getUserByUsername(username);

		ProjectRole r = ProjectRole.PROJECT_USER;

		if (representation.containsKey(USER_ROLE_KEY)) {
			r = ProjectRole.fromString(representation.get(USER_ROLE_KEY));
		}

		ProjectMetadataRole metadataRole = ProjectMetadataRole.LEVEL_1;

		if (representation.containsKey(METADATA_ROLE_KEY)) {
			metadataRole = ProjectMetadataRole.fromString(representation.get(METADATA_ROLE_KEY));
		}

		// then add the user to the project with the specified role.
		Join<Project, User> joined = projectService.addUserToProject(p, u, r, metadataRole);
		LabelledRelationshipResource<Project, User> lrr = new LabelledRelationshipResource<Project, User>(
				joined.getLabel(), joined);
		// prepare a link to the user
		lrr.add(linkTo(RESTUsersController.class).slash(u.getUsername())
				.withSelfRel());
		// prepare a link to the user as added to the project
		lrr.add(linkTo(
				methodOn(RESTProjectUsersController.class).removeUserFromProject(projectId, u.getUsername())).withRel(
				RESTGenericController.REL_RELATIONSHIP));
		// prepare a link back to the user collection of the project
		lrr.add(linkTo(methodOn(RESTProjectUsersController.class).getUsersForProject(projectId)).withRel(
				REL_PROJECT_USERS));
		// prepare a link back to the project
		lrr.add(linkTo(RESTProjectsController.class).slash(projectId)
				.withRel(RESTProjectsController.REL_PROJECT));
		String location = linkTo(RESTProjectsController.class).slash(projectId)
				.slash("users")
				.slash(username)
				.withSelfRel()
				.getHref();
		// add a location header and set the response status.
		response.addHeader(HttpHeaders.LOCATION, location);
		response.setStatus(HttpStatus.CREATED.value());
		// prepare the response for the client
		ResponseResource<LabelledRelationshipResource<Project, User>> responseObject = new ResponseResource<>(lrr);

		return responseObject;
	}

	/**
	 * Remove the relationship between a {@link Project} and a {@link User}.
	 *
	 * @param projectId the {@link Project} identifier to remove the {@link User} from.
	 * @param userId    the {@link User} identifier to remove from the {@link Project}.
	 * @return a response including links back to the {@link Project} and the {@link User} collection for the {@link
	 * Project}.
	 * @throws ProjectWithoutOwnerException if removing this user will leave the project without an owner
	 */
	@Operation(operationId = "removeUserFromProject", summary = "Remove a user from a given project", description = "Remove a user from a given project.", tags = "projects")
	@RequestMapping(value = "/api/projects/{projectId}/users/{userId}", method = RequestMethod.DELETE)
	public ResponseResource<RootResource> removeUserFromProject(@PathVariable Long projectId,
			@PathVariable String userId) throws ProjectWithoutOwnerException {
		// Read the project and user from the database
		Project p = projectService.read(projectId);
		User u = userService.getUserByUsername(userId);

		// ask the project service to remove the user from the project
		projectService.removeUserFromProject(p, u);

		// prepare a link back to the user collection of the project
		RootResource response = new RootResource();
		response.add(linkTo(methodOn(RESTProjectUsersController.class).getUsersForProject(projectId)).withRel(
				REL_PROJECT_USERS));

		// prepare a link back to the project
		response.add(linkTo(RESTProjectsController.class).slash(projectId)
				.withRel(RESTProjectsController.REL_PROJECT));

		// respond to the client
		ResponseResource<RootResource> responseObject = new ResponseResource<>(response);

		return responseObject;
	}
}

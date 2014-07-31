package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableList;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@Controller
@RequestMapping("/projects")
public class ProjectMembersController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersController.class);

	private static final String ACTIVE_NAV_MEMBERS = "members";
	private static final String PROJECTS_DIR = "projects/";
	private static final String ACTIVE_NAV = "activeNav";

	public static final String PROJECT_MEMBERS_PAGE = PROJECTS_DIR + "project_members";

	private final ProjectControllerUtils projectUtils;
	private final ProjectService projectService;
	private final UserService userService;

	private static final List<ProjectRole> projectRoles = ImmutableList.of(ProjectRole.PROJECT_USER,
			ProjectRole.PROJECT_OWNER);

	@Autowired
	public ProjectMembersController(ProjectControllerUtils projectUtils, ProjectService projectService,
			UserService userService) {
		this.projectUtils = projectUtils;
		this.projectService = projectService;
		this.userService = userService;
	}

	/**
	 * Gets the name of the template for the project members page. Populates the
	 * template with standard info.
	 *
	 * @param model
	 *            {@link Model}
	 * @param principal
	 *            {@link Principal}
	 * @param projectId
	 *            Id for the project to show the users for
	 * @return The name of the project members page.
	 */
	@RequestMapping(value = "/{projectId}/members", method = RequestMethod.GET)
	public String getProjectUsersPage(final Model model, final Principal principal, @PathVariable Long projectId) {
		logger.trace("Getting project members for project " + projectId);
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_MEMBERS);
		model.addAttribute("projectRoles", projectRoles);
		return PROJECT_MEMBERS_PAGE;
	}

	@RequestMapping(value = "/{projectId}/members", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId,'isProjectOwner')")
	@ResponseBody
	public void addProjectMember(@PathVariable Long projectId, @RequestParam Long userId,
			@RequestParam String projectRole) {
		logger.trace("Adding user " + userId + " to project " + projectId);
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		ProjectRole role = ProjectRole.fromString(projectRole);

		projectService.addUserToProject(project, user, role);
	}

	@RequestMapping("/{projectId}/ajax/availablemembers")
	@ResponseBody
	public Map<Long, String> getUsersAvailableForProject(@PathVariable Long projectId, @RequestParam String term) {
		Project project = projectService.read(projectId);
		List<User> usersAvailableForProject = userService.getUsersAvailableForProject(project);
		Map<Long, String> users = new HashMap<>();
		for (User user : usersAvailableForProject) {
			if (user.getLabel().toLowerCase().contains(term.toLowerCase())) {
				users.put(user.getId(), user.getLabel());
			}
		}

		return users;
	}

	/**
	 * Remove a user from a project
	 * 
	 * @param projectId
	 *            The project to remove from
	 * @param userId
	 *            The user to remove
	 * @return
	 * @throws ProjectWithoutOwnerException
	 * @throws ProjectSelfEditException
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId,'isProjectOwner')")
	@RequestMapping("{projectId}/members/remove")
	@ResponseBody
	public void removeUser(@PathVariable Long projectId, @RequestParam Long userId, Principal principal)
			throws ProjectWithoutOwnerException, ProjectSelfEditException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);

		if (user.getUsername().equals(principal.getName())) {
			throw new ProjectSelfEditException("You cannot remove yourself from a project.");
		}

		projectService.removeUserFromProject(project, user);
	}

	/**
	 * Update a user's role on a project
	 * 
	 * @param projectId
	 *            The ID of the project
	 * @param userId
	 *            The ID of the user
	 * @param projectRole
	 *            The role to set
	 * @throws ProjectWithoutOwnerException
	 * @throws ProjectSelfEditException
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId,'isProjectOwner')")
	@RequestMapping("{projectId}/members/editrole")
	@ResponseBody
	public void updateUserRole(@PathVariable Long projectId, @RequestParam Long userId,
			@RequestParam String projectRole, Principal principal) throws ProjectWithoutOwnerException,
			ProjectSelfEditException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);

		if (user.getUsername().equals(principal.getName())) {
			throw new ProjectSelfEditException("You cannot edit your own role on a project.");
		}

		ProjectRole role = ProjectRole.fromString(projectRole);

		projectService.updateUserProjectRole(project, user, role);
	}
}

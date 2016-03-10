package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesUtils;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller for handling project/members views and functions
 * 
 *
 */
@Controller
@RequestMapping("/projects")
public class ProjectMembersController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersController.class);

	private static final String ACTIVE_NAV_MEMBERS = "members";
	private static final String PROJECTS_DIR = "projects/";
	private static final String ACTIVE_NAV = "activeNav";

	public static final String PROJECT_MEMBERS_PAGE = PROJECTS_DIR + "project_members";
	private static final String REMOVE_USER_MODAL = "projects/remove-user-modal";

	private final ProjectControllerUtils projectUtils;
	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;

	private static final List<ProjectRole> projectRoles = ImmutableList.of(ProjectRole.PROJECT_USER,
			ProjectRole.PROJECT_OWNER);

	@Autowired
	public ProjectMembersController(ProjectControllerUtils projectUtils, ProjectService projectService,
			UserService userService, MessageSource messageSource) {
		this.projectUtils = projectUtils;
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
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

	/**
	 * Add a member to a project
	 * 
	 * @param projectId
	 *            The ID of the project
	 * @param userId
	 *            The ID of the user
	 * @param projectRole
	 *            The role for the user on the project
	 * @param locale
     *  		  the reported locale of the browser
     * @return map for showing success message.
	 */
	@RequestMapping(value = "/{projectId}/members", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId,'isProjectOwner')")
	@ResponseBody
	public Map<String, String> addProjectMember(@PathVariable Long projectId, @RequestParam Long userId,
			@RequestParam String projectRole, Locale locale) {
		logger.trace("Adding user " + userId + " to project " + projectId);
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		ProjectRole role = ProjectRole.fromString(projectRole);

		projectService.addUserToProject(project, user, role);
		return ImmutableMap.of(
				"result", messageSource.getMessage("project.members.add.success", new Object[]{user.getLabel(), project.getLabel()}, locale)
		);
	}

	/**
	 * Search the list of users who could be added to a project
	 * 
	 * @param projectId
	 *            The ID of the project
	 * @param term
	 *            A search term
	 * @return A {@code Map<Long,String>} of the userID and user label
	 */
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
	 * @param principal
	 *            a reference to the logged in user.
	 * @throws ProjectWithoutOwnerException
	 *             if removing the user leaves the project with no owner
	 * @throws ProjectSelfEditException
	 *             if a user is trying to remove themself from the project.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId,'isProjectOwner')")
	@RequestMapping(path = "{projectId}/members/{userId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> removeUser(final @PathVariable Long projectId, final @PathVariable Long userId,
			final Principal principal, final Locale locale) {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);

		if (user.getUsername().equals(principal.getName())) {
			return ImmutableMap.of("failure", messageSource.getMessage("project.members.edit.selfmessage",
					new Object[] { }, locale));
		}

		try {
			projectService.removeUserFromProject(project, user);
			return ImmutableMap.of("success", messageSource.getMessage("project.members.edit.remove.success",
					new Object[] { user.getLabel() }, locale));
		} catch (final ProjectWithoutOwnerException e) {
			return ImmutableMap.of("failure", messageSource.getMessage("project.members.edit.remove.nomanager",
					new Object[] { user.getLabel() }, locale));
		}
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
	 * @param principal
	 *            a reference to the logged in user.
	 * @throws ProjectWithoutOwnerException
	 *             if changing the user role on the project leaves it without an
	 *             owner
	 * @throws ProjectSelfEditException
	 *             if a user tries to change their own role on a project.
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

	@RequestMapping(value = "/ajax/{projectId}/members")
	public @ResponseBody DatatablesResponse<Join<Project, User>> getGroupMembers(
			final @DatatablesParams DatatablesCriterias criteria, final @PathVariable Long projectId) {
		final Project p = projectService.read(projectId);
		final int currentPage = DatatablesUtils.getCurrentPage(criteria);
		final Map<String, Object> sortProperties = DatatablesUtils.getSortProperties(criteria);
		final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
		final String sortName = sortProperties.get("sort_string").toString().replaceAll("object.", "user.")
				.replaceAll("label", "username");
		final String searchString = criteria.getSearch();
		
		final Page<Join<Project, User>> users = userService.searchUsersForProject(p, searchString, currentPage,
				criteria.getLength(), direction, sortName);
		final DataSet<Join<Project, User>> usersDataSet = new DataSet<>(users.getContent(), users.getTotalElements(),
				users.getTotalElements());
		
		return DatatablesResponse.build(usersDataSet, criteria);
	}
	
	/**
	 * Get a string to tell the user which group they're going to delete.
	 * 
	 * @param userGroupId
	 *            the user group that's about to be deleted.
	 * @param locale
	 *            the locale of the browser.
	 * @return a message indicating which group is going to be deleted.
	 */
	@RequestMapping(path = "/removeUserModal", method = RequestMethod.POST)
	public String getRemoveUserModal(final @RequestParam Long userId, final Model model) {
		final User user = userService.read(userId);
		model.addAttribute("user", user);
		return REMOVE_USER_MODAL;
	}
	
}

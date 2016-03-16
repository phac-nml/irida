package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.exceptions.ProjectSelfEditException;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesUtils;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
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
	public static final String PROJECT_GROUPS_PAGE = PROJECTS_DIR + "project_members_groups";
	private static final String REMOVE_USER_MODAL = "projects/templates/remove-user-modal";

	private final ProjectControllerUtils projectUtils;
	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;
	private final UserGroupService userGroupService;

	private static final List<ProjectRole> projectRoles = ImmutableList.of(ProjectRole.PROJECT_USER,
			ProjectRole.PROJECT_OWNER);

	@Autowired
	public ProjectMembersController(final ProjectControllerUtils projectUtils, final ProjectService projectService,
			final UserService userService, final UserGroupService userGroupService, final MessageSource messageSource) {
		this.projectUtils = projectUtils;
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
		this.userGroupService = userGroupService;
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
	@RequestMapping(value = "/{projectId}/groups", method = RequestMethod.GET)
	public String getProjectGroupsPage(final Model model, final Principal principal, @PathVariable Long projectId) {
		logger.trace("Getting project members for project " + projectId);
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_MEMBERS);
		model.addAttribute("projectRoles", projectRoles);
		return PROJECT_GROUPS_PAGE;
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
	 *            the reported locale of the browser
	 * @return map for showing success message.
	 */
	@RequestMapping(value = "/{projectId}/members", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> addProjectMember(@PathVariable Long projectId, @RequestParam Long memberId,
			@RequestParam String projectRole, Locale locale) {
		logger.trace("Adding user " + memberId + " to project " + projectId);
		Project project = projectService.read(projectId);
		User user = userService.read(memberId);
		ProjectRole role = ProjectRole.fromString(projectRole);

		projectService.addUserToProject(project, user, role);
		return ImmutableMap.of("result", messageSource.getMessage("project.members.add.success",
				new Object[] { user.getLabel(), project.getLabel() }, locale));
	}

	/**
	 * Add a group to a project
	 * 
	 * @param projectId
	 *            The ID of the project
	 * @param userId
	 *            The ID of the user
	 * @param projectRole
	 *            The role for the user on the project
	 * @param locale
	 *            the reported locale of the browser
	 * @return map for showing success message.
	 */
	@RequestMapping(value = "/{projectId}/groups", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> addProjectGroupMember(@PathVariable Long projectId, @RequestParam Long memberId,
			@RequestParam String projectRole, Locale locale) {
		logger.trace("Adding user " + memberId + " to project " + projectId);
		final Project project = projectService.read(projectId);
		final UserGroup userGroup = userGroupService.read(memberId);
		final ProjectRole role = ProjectRole.fromString(projectRole);

		projectService.addUserGroupToProject(project, userGroup, role);
		return ImmutableMap.of("result", messageSource.getMessage("project.members.add.success",
				new Object[] { userGroup.getLabel(), project.getLabel() }, locale));
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
	public Collection<User> getUsersAvailableForProject(@PathVariable Long projectId, @RequestParam String term) {
		final Project project = projectService.read(projectId);
		final List<User> usersAvailableForProject = userService.getUsersAvailableForProject(project, term);

		return usersAvailableForProject;
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
	@RequestMapping("/{projectId}/ajax/availablegroupmembers")
	@ResponseBody
	public Collection<UserGroup> getGroupsAvailableForProject(@PathVariable Long projectId, @RequestParam String term) {
		final Project project = projectService.read(projectId);
		final List<UserGroup> groupsAvailableForProject = userGroupService.getUserGroupsNotOnProject(project, term);

		return groupsAvailableForProject;
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
	@RequestMapping(path = "{projectId}/members/{userId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> removeUser(final @PathVariable Long projectId, final @PathVariable Long userId,
			final Principal principal, final Locale locale) {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);

		if (user.getUsername().equals(principal.getName())) {
			return ImmutableMap.of("failure",
					messageSource.getMessage("project.members.edit.selfmessage", new Object[] {}, locale));
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
	 * Remove a user group from a project
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
	@RequestMapping(path = "{projectId}/groups/{userId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> removeUserGroup(final @PathVariable Long projectId, final @PathVariable Long userId,
			final Principal principal, final Locale locale) {
		final Project project = projectService.read(projectId);
		final UserGroup userGroup = userGroupService.read(userId);

		projectService.removeUserGroupFromProject(project, userGroup);
		return ImmutableMap.of("success", messageSource.getMessage("project.members.edit.remove.success",
				new Object[] { userGroup.getLabel() }, locale));
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
	@RequestMapping(path = "{projectId}/members/editrole/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateUserRole(final @PathVariable Long projectId, final @PathVariable Long userId,
			final @RequestParam String projectRole, final Principal principal, final Locale locale) {
		final Project project = projectService.read(projectId);
		final User user = userService.read(userId);
		final ProjectRole role = ProjectRole.fromString(projectRole);
		final String roleName = messageSource.getMessage("projectRole." + projectRole, new Object[] {}, locale);

		if (user.getUsername().equals(principal.getName())) {
			return ImmutableMap.of("failure",
					messageSource.getMessage("project.members.edit.selfmessage", new Object[] {}, locale));
		}

		try {
			projectService.updateUserProjectRole(project, user, role);
			return ImmutableMap.of("success", messageSource.getMessage("project.members.edit.role.success",
					new Object[] { user.getLabel(), roleName }, locale));
		} catch (final ProjectWithoutOwnerException e) {
			return ImmutableMap.of("failure", messageSource.getMessage("project.members.edit.role.failure.nomanager",
					new Object[] { user.getLabel(), roleName }, locale));
		}
	}

	/**
	 * Update a user group's role on a project
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
	@RequestMapping(path = "{projectId}/groups/editrole/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateUserGroupRole(final @PathVariable Long projectId, final @PathVariable Long userId,
			final @RequestParam String projectRole, final Principal principal, final Locale locale) {
		final Project project = projectService.read(projectId);
		final UserGroup user = userGroupService.read(userId);
		final ProjectRole role = ProjectRole.fromString(projectRole);
		final String roleName = messageSource.getMessage("projectRole." + projectRole, new Object[] {}, locale);
		projectService.updateUserGroupProjectRole(project, user, role);
		return ImmutableMap.of("success", messageSource.getMessage("project.members.edit.role.success",
				new Object[] { user.getLabel(), roleName }, locale));
	}

	/**
	 * Get a page of users on the project for display in a datatable.
	 * 
	 * @param criteria
	 *            the datatables criteria for filtering/sorting members
	 * @param projectId
	 *            the id of the project we're looking at
	 * @return a page of users on the project
	 */
	@RequestMapping(value = "/ajax/{projectId}/members")
	public @ResponseBody DatatablesResponse<Join<Project, User>> getProjectUserMembers(
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
	 * Get a page of groups on the project for display in a datatable.
	 * 
	 * @param criteria
	 *            the datatables criteria for filtering/sorting groups
	 * @param projectId
	 *            the id of the project we're looking at
	 * @return a page of groups on the project.
	 */
	@RequestMapping(value = "/ajax/{projectId}/groups")
	public @ResponseBody DatatablesResponse<UserGroupProjectJoin> getProjectGroupMembers(
			final @DatatablesParams DatatablesCriterias criteria, final @PathVariable Long projectId) {
		final Project p = projectService.read(projectId);
		final int currentPage = DatatablesUtils.getCurrentPage(criteria);
		final Map<String, Object> sortProperties = DatatablesUtils.getSortProperties(criteria);
		final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
		final String sortName = sortProperties.get("sort_string").toString().replaceAll("object.", "userGroup.");
		final String searchString = criteria.getSearch();

		final Page<UserGroupProjectJoin> users = userGroupService.getUserGroupsForProject(searchString, p, currentPage,
				criteria.getLength(), direction, sortName);
		final DataSet<UserGroupProjectJoin> usersDataSet = new DataSet<>(users.getContent(), users.getTotalElements(),
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
	public String getRemoveUserModal(final @RequestParam Long memberId, final Model model) {
		final User user = userService.read(memberId);
		model.addAttribute("member", user);
		return REMOVE_USER_MODAL;
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
	@RequestMapping(path = "/removeUserGroupModal", method = RequestMethod.POST)
	public String getRemoveUserGroupModal(final @RequestParam Long memberId, final Model model) {
		final UserGroup userGroup = userGroupService.read(memberId);
		model.addAttribute("member", userGroup);
		return REMOVE_USER_MODAL;
	}
}

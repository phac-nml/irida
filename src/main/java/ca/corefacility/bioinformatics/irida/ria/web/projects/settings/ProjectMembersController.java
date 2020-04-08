package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProjectGroup;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProjectMember;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Controller for handling project/members views and functions
 */
@Controller
@RequestMapping("/projects")
public class ProjectMembersController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersController.class);

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
	@RequestMapping(value = { "/{projectId}/settings/members", "/{projectId}/settings/members/edit" })
	public String getProjectUsersPage(final Model model, final Principal principal, @PathVariable Long projectId) {

		Project project = projectService.read(projectId);
		model.addAttribute("project", project);

		projectUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute("projectRoles", projectRoles);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "members");
		return "projects/settings/pages/members";
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
	@RequestMapping(value = "/{projectId}/settings/groups", method = RequestMethod.GET)
	public String getProjectGroupsPage(final Model model, final Principal principal, @PathVariable Long projectId) {
		logger.trace("Getting project members for project " + projectId);
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("projectRoles", projectRoles);
		model.addAttribute("page", "groups");
		return "projects/settings/pages/groups";
	}

	/**
	 * Add a group to a project
	 * 
	 * @param projectId
	 *            The ID of the project
	 * @param memberId
	 *            The ID of the user
	 * @param projectRole
	 *            The role for the user on the project
	 * @param locale
	 *            the reported locale of the browser
	 * @return map for showing success message.
	 */
	@RequestMapping(value = "/{projectId}/settings/groups", method = RequestMethod.POST)
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
	@RequestMapping("/{projectId}/settings/ajax/availablegroupmembers")
	@ResponseBody
	public Collection<UserGroup> getGroupsAvailableForProject(@PathVariable Long projectId, @RequestParam String term) {
		final Project project = projectService.read(projectId);
		final List<UserGroup> groupsAvailableForProject = userGroupService.getUserGroupsNotOnProject(project, term);

		return groupsAvailableForProject;
	}

	/**
	 * Remove a user group from a project
	 *
	 * @param projectId The project to remove from
	 * @param userId    The user to remove
	 * @param locale    Locale of the logged in user
	 * @return Success or error message
	 */
	@RequestMapping(path = "{projectId}/settings/groups/{userId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> removeUserGroup(final @PathVariable Long projectId, final @PathVariable Long userId,
			final Locale locale) {
		final Project project = projectService.read(projectId);
		final UserGroup userGroup = userGroupService.read(userId);

		try {
			projectService.removeUserGroupFromProject(project, userGroup);
			return ImmutableMap.of("success", messageSource.getMessage("project.members.edit.remove.success",
					new Object[] { userGroup.getLabel() }, locale));
		} catch (final ProjectWithoutOwnerException e) {
			return ImmutableMap.of("failure", messageSource.getMessage("project.members.edit.remove.nomanager",
					new Object[] { userGroup.getLabel() }, locale));
		}
	}

	/**
	 * Update a user group's role on a project
	 *
	 * @param projectId   The ID of the project
	 * @param userId      The ID of the user
	 * @param projectRole The role to set
	 * @param locale      Locale of the logged in user
	 * @return Success or failure message
	 */
	@RequestMapping(path = "{projectId}/settings/groups/editrole/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateUserGroupRole(final @PathVariable Long projectId, final @PathVariable Long userId,
			final @RequestParam String projectRole, final Locale locale) {
		final Project project = projectService.read(projectId);
		final UserGroup userGroup = userGroupService.read(userId);
		final ProjectRole role = ProjectRole.fromString(projectRole);
		final String roleName = messageSource.getMessage("projectRole." + projectRole, new Object[] {}, locale);

		try {
			projectService.updateUserGroupProjectRole(project, userGroup, role);
			return ImmutableMap.of("success", messageSource.getMessage("project.members.edit.role.success",
					new Object[] { userGroup.getLabel(), roleName }, locale));
		} catch (final ProjectWithoutOwnerException e) {
			return ImmutableMap.of("failure", messageSource.getMessage("project.members.edit.role.failure.nomanager",
					new Object[] { userGroup.getLabel(), roleName }, locale));
		}
	}

	/**
	 * Get a page of groups on the project for display in a DataTable.
	 *
	 * @param params
	 *            the datatables criteria for filtering/sorting groups
	 * @param projectId
	 *            the id of the project we're looking at
	 * @return a {@link DataTablesResponseModel} of groups on the project
	 */
	@RequestMapping(value = "/{projectId}/settings/ajax/groups")
	@ResponseBody
	public DataTablesResponse getProjectGroupMembers(@DataTablesRequest DataTablesParams params,
			final @PathVariable Long projectId) {
		final Project project = projectService.read(projectId);

		final Page<UserGroupProjectJoin> userGroupsForProject = userGroupService.getUserGroupsForProject(
				params.getSearchValue(), project, params.getCurrentPage(), params.getLength(), params.getSort());
		List<DataTablesResponseModel> responseModels = new ArrayList<>();
		for (UserGroupProjectJoin userGroupProjectJoin : userGroupsForProject) {
			responseModels.add(new DTProjectGroup(userGroupProjectJoin));
		}

		return new DataTablesResponse(params, userGroupsForProject, responseModels);
	}

	/**
	 * Get a string to tell the user which group they're going to delete.
	 * 
	 * @param memberId
	 *            the user group that's about to be deleted.
	 * @param model
	 *            Model for rendering the view
	 * @return Name of the user group removal modal
	 */
	@RequestMapping(path = "/settings/removeUserGroupModal", method = RequestMethod.POST)
	public String getRemoveUserGroupModal(final @RequestParam Long memberId, final Model model) {
		final UserGroup userGroup = userGroupService.read(memberId);
		model.addAttribute("member", userGroup);
		return REMOVE_USER_MODAL;
	}
}

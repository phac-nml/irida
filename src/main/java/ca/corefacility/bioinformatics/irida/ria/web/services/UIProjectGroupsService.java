package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.NewProjectGroupRequest;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

@Component
public class UIProjectGroupsService {
	private final ProjectService projectService;
	private final UserGroupService userGroupService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectGroupsService(ProjectService projectService, UserGroupService userGroupService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.userGroupService = userGroupService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a paged listing of project groups based on parameters set in the table request.
	 *
	 * @param projectId    identifier for the current project
	 * @param tableRequest details about the current page of the table
	 * @return sorted and filtered list of project user groups
	 */
	public TableResponse<ProjectGroupTableModel> getProjectGroups(Long projectId, TableRequest tableRequest) {
		Project project = projectService.read(projectId);
		Page<UserGroupProjectJoin> joins = userGroupService.getUserGroupsForProject(tableRequest.getSearch(), project,
				tableRequest.getCurrent(), tableRequest.getPageSize(), tableRequest.getSort());
		List<ProjectGroupTableModel> groups = joins.getContent()
				.stream()
				.map(j -> new ProjectGroupTableModel(j.getObject(), j.getProjectRole()
						.toString(), j.getCreatedDate()))
				.collect(Collectors.toList());
		return new TableResponse<>(groups, joins.getTotalElements());
	}

	/**
	 * Search for user groups that are not currently on the project
	 *
	 * @param projectId identifier for the current project
	 * @param query     to search user group names by
	 * @return a filtered list of user groups
	 */
	public List<ProjectGroupTableModel> searchAvailableGroups(Long projectId, String query) {
		Project project = projectService.read(projectId);
		List<UserGroup> userGroups = userGroupService.getUserGroupsNotOnProject(project, query);
		return userGroups.stream()
				.map(u -> new ProjectGroupTableModel(u, null, null))
				.collect(Collectors.toList());
	}

	/**
	 * Add a new user group to the current project
	 *
	 * @param projectId identifier for the current project
	 * @param request   information about the user group (identifier and role)
	 * @param locale    currently logged in users locale
	 * @return an internationalized message stating the result of adding the new group
	 */
	public String addUserGroupToProject(Long projectId, NewProjectGroupRequest request, Locale locale) {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(request.getId());
		ProjectRole role = ProjectRole.fromString(request.getRole());
		projectService.addUserGroupToProject(project, group, role);
		return messageSource.getMessage("server.AddGroup.success",
				new Object[] { group.getLabel(), project.getLabel() }, locale);
	}

	/**
	 * Updated an user groups role on the current project
	 *
	 * @param projectId identifier for the current project
	 * @param groupId   identifier for the user group
	 * @param role      string representation of the new role
	 * @param locale    currently logged in users locale
	 * @return an internationalized message stating the result of the change in role.
	 */
	public String updateGroupRoleOnProject(Long projectId, Long groupId, String role, Locale locale)
			throws UIProjectWithoutOwnerException {
		Project project = projectService.read(projectId);
		UserGroup userGroup = userGroupService.read(groupId);
		ProjectRole projectRole = ProjectRole.valueOf(role);
		String roleName = messageSource.getMessage("projectRole." + role, new Object[] {}, locale);
		try {
			projectService.updateUserGroupProjectRole(project, userGroup, projectRole);
			return messageSource.getMessage("server.ProjectRoleSelect.success",
					new Object[] { userGroup.getLabel(), roleName }, locale);
		} catch (ProjectWithoutOwnerException e) {
			throw new UIProjectWithoutOwnerException(messageSource.getMessage("server.ProjectRoleSelect.error",
					new Object[] { userGroup.getLabel(), roleName }, locale));
		}
	}

	/**
	 * Remove a user group from the current project
	 *
	 * @param projectId identifier for the current project
	 * @param groupId   identifier for the group to remove
	 * @param locale    currently logged in users locale
	 * @return an internationalized message stating the result of removing the user group
	 */
	public String removeUserGroupFromProject(Long projectId, Long groupId, Locale locale)
			throws UIProjectWithoutOwnerException {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(groupId);
		try {
			projectService.removeUserGroupFromProject(project, group);
			return messageSource.getMessage("server.RemoveGroup.success", new Object[] { group.getLabel() }, locale);
		} catch (ProjectWithoutOwnerException e) {
			throw new UIProjectWithoutOwnerException(
					messageSource.getMessage("server.RemoveGroup.error", new Object[] { group.getLabel() }, locale));
		}
	}
}

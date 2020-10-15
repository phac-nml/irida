package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMemberRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectUserGroupsTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

/**
 * UI Service to handle requests for project user groups.
 */
@Component
public class UIProjectUserGroupsService {
	private final ProjectService projectService;
	private final UserGroupService userGroupService;
	private final MessageSource messageSource;

	public UIProjectUserGroupsService(ProjectService projectService, UserGroupService userGroupService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.userGroupService = userGroupService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a table page of {@link ProjectUserGroupsTableModel}
	 *
	 * @param projectId Identifier for a {@link Project}
	 * @param request   {@link TableRequest} details about the current page of the table
	 * @return {@link TableResponse}
	 */
	public TableResponse<ProjectUserGroupsTableModel> getUserGroupsForProject(Long projectId, TableRequest request) {
		Project project = projectService.read(projectId);
		Page<UserGroupProjectJoin> userGroupJoins = userGroupService.getUserGroupsForProject(request.getSearch(),
				project, request.getCurrent(), request.getPageSize(), request.getSort());
		List<ProjectUserGroupsTableModel> groups = userGroupJoins.getContent()
				.stream()
				.map(j -> new ProjectUserGroupsTableModel(j.getObject(), j.getProjectRole()
						.toString(), j.getCreatedDate()))
				.collect(Collectors.toList());
		return new TableResponse<>(groups, userGroupJoins.getTotalElements());
	}

	/**
	 * Remove a {@link UserGroup} from a {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project}
	 * @param groupId   Identifier for an {@link UserGroup}
	 * @param locale    current users {@link Locale}
	 * @return message to user about the result of removing the user group
	 */
	public String removeUserGroupFromProject(long projectId, long groupId, Locale locale) {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(groupId);
		try {
			projectService.removeUserGroupFromProject(project, group);
			return messageSource.getMessage("server.usergroups.remove.success", new Object[] { group.getLabel() },
					locale);
		} catch (ProjectWithoutOwnerException e) {
			return messageSource.getMessage("server.usergroups.remove.error", new Object[] { group.getLabel() },
					locale);
		}
	}

	/**
	 * Get a list of {@link UserGroup}s that are not on the current {@link Project}
	 *
	 * @param projectId Identifier for the current {@link Project}
	 * @param query     Filter string to search the existing {@link UserGroup}s by
	 * @return List of {@link UserGroup}
	 */
	public List<UserGroup> getAvailableUserGroupsForProject(Long projectId, String query) {
		Project project = projectService.read(projectId);
		return userGroupService.getUserGroupsNotOnProject(project, query);
	}

	/**
	 * Add a {@link UserGroup} to the current {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project}
	 * @param request   Identifier for an {@link UserGroup}
	 * @param locale    Current users {@link Locale}
	 * @return message to user about the outcome of adding the user group to the project
	 */
	public String addUserGroupToProject(Long projectId, NewMemberRequest request, Locale locale) {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(request.getId());
		ProjectRole role = ProjectRole.fromString(request.getRole());
		projectService.addUserGroupToProject(project, group, role);
		return messageSource.getMessage("server.usergroups.add", new Object[] { group.getLabel() }, locale);
	}

	/**
	 * Update the {@link ProjectRole} of a {@link UserGroup} on the current {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project}
	 * @param groupId   Identifier for an {@link UserGroup}
	 * @param role      Role to update the user group to
	 * @param locale    Current users {@link Locale}
	 * @return message to user about the result of the update
	 * @throws ProjectWithoutOwnerException thrown when updating the role will result in the project to have no owner
	 */
	public String updateUserGroupRoleOnProject(Long projectId, Long groupId, String role, Locale locale)
			throws ProjectWithoutOwnerException {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(groupId);
		ProjectRole projectRole = ProjectRole.fromString(role);
		String roleString = messageSource.getMessage("projectRole." + role, new Object[] {}, locale);

		try {
			projectService.updateUserGroupProjectRole(project, group, projectRole);
			return messageSource.getMessage("server.usergroups.update.success",
					new Object[] { group.getLabel(), roleString }, locale);
		} catch (ProjectWithoutOwnerException e) {
			throw new ProjectWithoutOwnerException(messageSource.getMessage("server.usergroups.update-role.error", new Object[] { group.getLabel() },
					locale));
		}
	}
}

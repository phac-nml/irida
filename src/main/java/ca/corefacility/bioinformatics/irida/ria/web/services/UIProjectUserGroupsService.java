package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMemberRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectUserGroupsTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
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
						.toString(), j.getMetadataRole()
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
		ProjectRole role = ProjectRole.fromString(request.getProjectRole());
		ProjectMetadataRole metadataRole = ProjectMetadataRole.fromString(request.getMetadataRole());
		projectService.addUserGroupToProject(project, group, role, metadataRole);
		return messageSource.getMessage("server.usergroups.add", new Object[] { group.getLabel() }, locale);
	}

	/**
	 * Update the {@link ProjectRole} of a {@link UserGroup} on the current {@link Project}
	 *
	 * @param projectId    Identifier for a {@link Project}
	 * @param groupId      Identifier for an {@link UserGroup}
	 * @param role         Role to update the user group to
	 * @param metadataRole metadata role to update for the group
	 * @param locale       Current users {@link Locale}
	 * @return message to user about the result of the update
	 * @throws UIProjectWithoutOwnerException thrown when updating the role will result in the project to have no owner
	 */
	public String updateUserGroupRoleOnProject(Long projectId, Long groupId, String role, String metadataRole,
			Locale locale) throws UIProjectWithoutOwnerException {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(groupId);
		ProjectRole projectRole = null;
		ProjectMetadataRole projectMetadataRole = null;
		String roleString;
		String updateSuccessMessage;

		if (!role.isEmpty()) {
			projectRole = ProjectRole.fromString(role);
			roleString = messageSource.getMessage("projectRole." + role, new Object[] {}, locale);
			updateSuccessMessage = messageSource.getMessage("server.update.projectRole.success",
					new Object[] { group.getLabel(), roleString }, locale);
		} else {
			projectMetadataRole = ProjectMetadataRole.fromString(metadataRole);
			roleString = messageSource.getMessage("metadataRole." + metadataRole, new Object[] {}, locale);
			updateSuccessMessage = messageSource.getMessage("server.update.metadataRole.success",
					new Object[] { group.getLabel(), roleString }, locale);
		}

		try {
			projectService.updateUserGroupProjectRole(project, group, projectRole, projectMetadataRole);
			return updateSuccessMessage;
		} catch (ProjectWithoutOwnerException e) {
			throw new UIProjectWithoutOwnerException(messageSource.getMessage("server.ProjectRoleSelect.error",
					new Object[] { group.getLabel(), roleString }, locale));
		}
	}
}

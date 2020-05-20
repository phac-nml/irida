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

	public TableResponse<ProjectUserGroupsTableModel> getUserGroupsForProject(Long projectId, TableRequest request) {
		Project project = projectService.read(projectId);
		Page<UserGroupProjectJoin> userGroupJoins = userGroupService.getUserGroupsForProject(request.getSearch(),
				project, request.getCurrent(), request.getPageSize(), request.getSort());
		List<ProjectUserGroupsTableModel> groups = userGroupJoins.getContent().stream().map(j -> new ProjectUserGroupsTableModel(j.getObject(), j.getProjectRole()
				.toString(), j.getCreatedDate())).collect(Collectors.toList());
		return new TableResponse<>(groups, userGroupJoins.getTotalElements());
	}

	public String removeUserGroupFromProject(long projectId, long groupId, Locale locale) {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(groupId);
		try {
			projectService.removeUserGroupFromProject(project, group);
			return messageSource.getMessage("server.usergroups.remove-group.success", new Object[] { group.getLabel() },
					locale);
		} catch (ProjectWithoutOwnerException e) {
			return messageSource.getMessage("server.usergroups.remove-group.error", new Object[] { group.getLabel() },
					locale);
		}
	}

	public List<UserGroup> getAvailableUserGroupsForProject(Long projectId, String query) {
		Project project = projectService.read(projectId);
		return userGroupService.getUserGroupsNotOnProject(project, query);
	}

	public String addUserGroupToProject(Long projectId, NewMemberRequest request, Locale locale) {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(request.getId());
		ProjectRole role = ProjectRole.fromString(request.getRole());
		projectService.addUserGroupToProject(project, group, role);
		return messageSource.getMessage("server.usergroups.add-group", new Object[] { group.getLabel() }, locale);
	}

	public String updateUserGroupRoleOnProject(Long projectId, Long groupId, String role, Locale locale)
			throws ProjectWithoutOwnerException {
		Project project = projectService.read(projectId);
		UserGroup group = userGroupService.read(groupId);
		ProjectRole projectRole = ProjectRole.fromString(role);
		String roleString = messageSource.getMessage("projectRole." + role, new Object[] {}, locale);

		try {
			projectService.updateUserGroupProjectRole(project, group, projectRole);
			return messageSource.getMessage("server.usergroups.update-role.success",
					new Object[] { group.getLabel(), roleString }, locale);
		} catch (ProjectWithoutOwnerException e) {
			throw new ProjectWithoutOwnerException(
					messageSource.getMessage("server.usergroups.update-role.error", new Object[] { group.getLabel() },
							locale));
		}
	}
}

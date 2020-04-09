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
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectMemberTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.NewProjectMemberRequest;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@Component
public class UIProjectMembersService {
	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectMembersService(ProjectService projectService, UserService userService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	public String removeUserFromProject(Long projectId, Long userId, Locale locale)
			throws UIProjectWithoutOwnerException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		try {
			projectService.removeUserFromProject(project, user);
			return messageSource.getMessage("server.RemoveMemberButton.success", new Object[] { user.getUsername() },
					locale);
		} catch (ProjectWithoutOwnerException e) {
			throw new UIProjectWithoutOwnerException(
					messageSource.getMessage("server.RemoveMemberButton.error", new Object[] { user.getUsername() },
							locale));
		}

	}

	public TableResponse<ProjectMemberTableModel> getProjectMembers(Long projectId, TableRequest tableRequest) {
		Project project = projectService.read(projectId);
		Page<Join<Project, User>> usersForProject = userService.searchUsersForProject(project, tableRequest.getSearch(),
				tableRequest.getCurrent(), tableRequest.getPageSize(), tableRequest.getSort());
		List<ProjectMemberTableModel> members = usersForProject.get()
				.map(join -> {
					ProjectUserJoin projectUserJoin = (ProjectUserJoin) join;
					return new ProjectMemberTableModel(join.getObject(), projectUserJoin.getProjectRole()
							.toString(), projectUserJoin.getCreatedDate());
				})
				.collect(Collectors.toList());
		return new TableResponse<>(members, usersForProject.getTotalElements());
	}

	public String updateUserRoleOnProject(Long projectId, Long userId, String role, Locale locale)
			throws UIProjectWithoutOwnerException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		ProjectRole projectRole = ProjectRole.fromString(role);
		String roleString = messageSource.getMessage("projectRole." + role, new Object[] {}, locale);

		try {
			projectService.updateUserProjectRole(project, user, projectRole);
			return messageSource.getMessage("server.ProjectRoleSelect.success",
					new Object[] { user.getLabel(), roleString }, locale);
		} catch (ProjectWithoutOwnerException e) {
			throw new UIProjectWithoutOwnerException(messageSource.getMessage("server.ProjectRoleSelect.error",
					new Object[] { user.getLabel(), roleString }, locale));
		}
	}

	public List<User> getAvailableUsersForProject(Long projectId, String query) {
		Project project = projectService.read(projectId);
		return userService.getUsersAvailableForProject(project, query);
	}

	public String addMemberToProject(Long projectId, NewProjectMemberRequest request, Locale locale) {
		Project project = projectService.read(projectId);
		User user = userService.read(request.getId());
		ProjectRole role = ProjectRole.fromString(request.getRole());
		projectService.addUserToProject(project, user, role);
		return messageSource.getMessage("project.members.add.success",
				new Object[] { user.getLabel(), project.getLabel() }, locale);
	}
}

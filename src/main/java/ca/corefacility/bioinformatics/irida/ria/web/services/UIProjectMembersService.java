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

/**
 * Service class for the UI for handling project members actions.
 */
@Component
public class UIProjectMembersService {
	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectMembersService(ProjectService projectService, UserService userService, MessageSource messageSource) {
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a paged listing of project members passed on parameters set in the table request.
	 *
	 * @param projectId    - identifier for the current project
	 * @param tableRequest - details about the current page of the table
	 * @return sorted and filtered list of project members
	 */
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

	/**
	 * Remove a user from the project
	 *
	 * @param projectId - identifier for the current project
	 * @param userId    - identifier for the user to remove from the project
	 * @param locale    - of the currently logged in user
	 * @return Message to display to the user about the outcome of removing a user from the project.
	 * @throws UIProjectWithoutOwnerException if removing the user will leave the project without a manage
	 */
	public String removeUserFromProject(Long projectId, Long userId, Locale locale) throws UIProjectWithoutOwnerException {
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

	/**
	 * Update a users role on a project
	 *
	 * @param projectId - identifier for the current project
	 * @param userId    - identifier for the user to remove from the project
	 * @param role      - to update the user to
	 * @param locale    - of the currently logged in user
	 * @return message to display to the user about the outcome of the change in role.
	 */
	public String updateUserRoleOnProject(Long projectId, Long userId, String role, Locale locale) throws UIProjectWithoutOwnerException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		ProjectRole projectRole = ProjectRole.fromString(role);
		String roleString = messageSource.getMessage("projectRole." + role, new Object[] {}, locale);

		try {
			projectService.updateUserProjectRole(project, user, projectRole);
			return messageSource.getMessage("server.ProjectRoleSelect.success", new Object[] { user.getLabel(), roleString }, locale);
		} catch (ProjectWithoutOwnerException e) {
			throw new UIProjectWithoutOwnerException(messageSource.getMessage("server.ProjectRoleSelect.error", new Object[] { user.getLabel(), roleString }, locale));
		}
	}

	/**
	 * Get a filtered list of available IRIDA instance users for this project
	 *
	 * @param projectId - identifier for the current project
	 * @param query     - search query to filter the users by
	 * @return List of filtered users.
	 */
	public List<User> getAvailableUsersForProject(Long projectId, String query) {
		Project project = projectService.read(projectId);
		return userService.getUsersAvailableForProject(project, query);
	}

	/**
	 * Add a user to a project
	 *
	 * @param projectId - identifier for the current project
	 * @param request   - details about the user to add to the project (id and role)
	 * @param locale    - of the currently logged in user
	 * @return message to display to the user about the outcome of adding the user to the project
	 */
	public String addMemberToProject(Long projectId, NewProjectMemberRequest request, Locale locale) {
		Project project = projectService.read(projectId);
		User user = userService.read(request.getId());
		ProjectRole role = ProjectRole.fromString(request.getRole());
		projectService.addUserToProject(project, user, role);
		return messageSource.getMessage("project.members.add.success", new Object[] { user.getLabel(), project.getLabel() }, locale);
	}
}

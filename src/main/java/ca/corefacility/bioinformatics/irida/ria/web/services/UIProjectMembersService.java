package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMemberRequest;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectMemberTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.utilities.ExceptionUtilities;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import org.springframework.transaction.TransactionSystemException;

/**
 * Service class for the UI for handling project members actions.
 */
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
		List<ProjectMemberTableModel> members = usersForProject.get().map(join -> {
			ProjectUserJoin projectUserJoin = (ProjectUserJoin) join;
			return new ProjectMemberTableModel(join.getObject(), projectUserJoin.getProjectRole().toString(),
					((ProjectUserJoin) join).getMetadataRole().toString(), projectUserJoin.getCreatedDate());
		}).collect(Collectors.toList());
		return new TableResponse<>(members, usersForProject.getTotalElements());
	}

	/**
	 * Remove a user from the project
	 *
	 * @param projectId - identifier for the current project
	 * @param userId    - identifier for the user to remove from the project
	 * @param locale    - of the currently logged in user
	 * @return Message to display to the user about the outcome of removing a user from the project.
	 * @throws UIProjectWithoutOwnerException if removing the user will leave the project without a manager
	 */
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

	/**
	 * Update a users role on a project
	 *
	 * @param projectId - identifier for the current project
	 * @param userId    - identifier for the user to remove from the project
	 * @param role      - {@link ProjectRole}  to update the user to
	 * @param locale    - of the currently logged in user
	 * @return message to display to the user about the outcome of the change in role.
	 * @throws UIProjectWithoutOwnerException if removing the user will leave the project without a manager
	 * @throws UIConstraintViolationException thrown when updating the project role to owner and the metadata role is
	 *                                        not set to the highest level
	 */
	public String updateUserRoleOnProject(Long projectId, Long userId, String role, Locale locale)
			throws UIProjectWithoutOwnerException, UIConstraintViolationException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		ProjectMetadataRole projectMetadataRole;
		ProjectRole projectRole = ProjectRole.fromString(role);
		String roleString = messageSource.getMessage("projectRole." + role, new Object[] {}, locale);

		/*
		 If a user's project role is set to collaborator we drop the metadata restriction to the lowest
		 level and have the project owner set it accordingly. If a role of owner is set then that user is
		 given full metadata permissions
		 */
		if (projectRole.equals(ProjectRole.PROJECT_OWNER)) {
			projectMetadataRole = ProjectMetadataRole.fromString("LEVEL_4");
		} else {
			projectMetadataRole = ProjectMetadataRole.fromString("LEVEL_1");
		}

		try {
			projectService.updateUserProjectRole(project, user, projectRole, projectMetadataRole);

			return messageSource.getMessage("server.update.projectRole.success",
					new Object[] { user.getLabel(), roleString }, locale);
		} catch (ProjectWithoutOwnerException e) {
			throw new UIProjectWithoutOwnerException(messageSource.getMessage("server.ProjectRoleSelect.error",
					new Object[] { user.getLabel(), roleString }, locale));
		} catch (TransactionSystemException e) {
			ExceptionUtilities.throwConstraintViolationException(e, locale, messageSource);
		}
		return null;
	}

	/**
	 * Update a users metadata role on a project
	 *
	 * @param projectId    - identifier for the current project
	 * @param userId       - identifier for the user to remove from the project
	 * @param metadataRole - {@link ProjectMetadataRole}  to update the user to
	 * @param locale       - of the currently logged in user
	 * @return message to display to the user about the outcome of the change in role.
	 * @throws UIConstraintViolationException thrown when updating the project role to owner and the metadata role is
	 *
	 */
	public String updateUserMetadataRoleOnProject(Long projectId, Long userId, String metadataRole, Locale locale)
			throws UIConstraintViolationException {
		Project project = projectService.read(projectId);
		User user = userService.read(userId);
		ProjectMetadataRole projectMetadataRole = ProjectMetadataRole.fromString(metadataRole);
		String roleString = messageSource.getMessage("metadataRole." + metadataRole, new Object[] {}, locale);

		try {
			projectService.updateUserProjectMetadataRole(project, user, projectMetadataRole);
			return messageSource.getMessage("server.update.metadataRole.success",
					new Object[] { user.getLabel(), roleString }, locale);
		} catch (TransactionSystemException e) {
			ExceptionUtilities.throwConstraintViolationException(e, locale, messageSource);
		}
		return null;

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
	public String addMemberToProject(Long projectId, NewMemberRequest request, Locale locale) {
		Project project = projectService.read(projectId);
		User user = userService.read(request.getId());
		ProjectRole role = ProjectRole.fromString(request.getProjectRole());
		ProjectMetadataRole metadataRole;

		if (role.equals(ProjectRole.PROJECT_OWNER)) {
			metadataRole = ProjectMetadataRole.LEVEL_4;
		} else {
			metadataRole = ProjectMetadataRole.fromString(request.getMetadataRole());
		}
		projectService.addUserToProject(project, user, role, metadataRole);
		return messageSource.getMessage("project.members.add.success",
				new Object[] { user.getLabel(), project.getLabel() }, locale);
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectUserTableModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@RestController
@RequestMapping("/ajax/projects/{projectId}/members")
public class ProjectMembersAjaxController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectMembersAjaxController.class);

	private final ProjectService projectService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public ProjectMembersAjaxController(ProjectService projectService, UserService userService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	@RequestMapping("")
	public TableResponse<ProjectUserTableModel> getProjectMembers(@PathVariable Long projectId,
			@RequestBody TableRequest request) {
		Project project = projectService.read(projectId);
		Page<Join<Project, User>> usersForProject = userService.searchUsersForProject(project, request.getSearch(),
				request.getCurrent(), request.getPageSize(), request.getSort());
		List<ProjectUserTableModel> members = usersForProject.get()
				.map(join -> {
					ProjectUserJoin projectUserJoin = (ProjectUserJoin) join;
					return new ProjectUserTableModel(join.getObject(), projectUserJoin.getProjectRole()
							.toString(), projectUserJoin.getCreatedDate());
				})
				.collect(Collectors.toList());
		return new TableResponse<>(members, usersForProject.getTotalElements());
	}

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeUserFromProject(@PathVariable Long projectId, @RequestParam Long id,
			Locale locale) {
		Project project = projectService.read(projectId);
		User user = userService.read(id);
		try {
			projectService.removeUserFromProject(project, user);
			return ResponseEntity.ok(messageSource.getMessage("server.RemoveMemberButton.success",
					new Object[] { user.getUsername() }, locale));
		} catch (EntityNotFoundException | ProjectWithoutOwnerException e) {
			logger.error("Error removing user id " + id + " from project " + projectId + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(messageSource.getMessage("server.RemoveMemberButton.error",
							new Object[] { user.getUsername() }, locale));
		}
	}

	@RequestMapping(value = "/role", method = RequestMethod.PUT)
	public ResponseEntity<String> updateUserRoleOnProject(@PathVariable Long projectId, @RequestParam Long id,
			String role, Locale locale) {
		Project project = projectService.read(projectId);
		User user = userService.read(id);
		ProjectRole projectRole = ProjectRole.fromString(role);
		String roleString = messageSource.getMessage("projectRole." + role, new Object[] {}, locale);

		try {
			projectService.updateUserProjectRole(project, user, projectRole);
			return ResponseEntity.ok(messageSource.getMessage("server.ProjectRoleSelect.success",
					new Object[] { user.getLabel(), roleString }, locale));
		} catch (ProjectWithoutOwnerException e) {
			logger.error("Error changing user id " + id + " in project " + projectId + " to " + roleString + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(messageSource.getMessage("server.ProjectRoleSelect.error",
							new Object[] { user.getLabel(), roleString }, locale));
		}
	}

	@RequestMapping("/available")
	public ResponseEntity<List<User>> getAvailableMembersForProject(@PathVariable Long projectId, @RequestParam String query) {
		Project project = projectService.read(projectId);
		return ResponseEntity.ok(userService.getUsersAvailableForProject(project, query));
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class ProjectMembersGroupsAjaxController {
	private final ProjectService projectService;
	private final UserService userService;

	@Autowired
	public ProjectMembersGroupsAjaxController(ProjectService projectService, UserService userService) {
		this.projectService = projectService;
		this.userService = userService;
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
}

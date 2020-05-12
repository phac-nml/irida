package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectUserGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

@Component
public class UIProjectUserGroupService {
	private final UserGroupService userGroupService;
	private final ProjectService projectService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectUserGroupService(UserGroupService userGroupService, ProjectService projectService,
			MessageSource messageSource) {
		this.userGroupService = userGroupService;
		this.projectService = projectService;
		this.messageSource = messageSource;
	}

	public TableResponse<ProjectUserGroupTableModel> getProjectUserGroups(Long projectId, TableRequest request) {
		Project project = projectService.read(projectId);
		/*
		Determine if the current user can manage this group
		 */
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		boolean isAdmin = user.getSystemRole()
				.equals(Role.ROLE_ADMIN);
		boolean canManage = isAdmin || projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER);

		Page<UserGroupProjectJoin> pagedGroups = userGroupService.getUserGroupsForProject(request.getSearch(), project,
				request.getCurrent(), request.getPageSize(), request.getSort());
		List<ProjectUserGroupTableModel> groups = pagedGroups.getContent()
				.stream()
				.map(join -> new ProjectUserGroupTableModel(join.getObject(), join.getCreatedDate(),
						join.getProjectRole()
								.toString(), canManage))
				.collect(Collectors.toList());
		return new TableResponse<>(groups, pagedGroups.getTotalElements());
	}
}

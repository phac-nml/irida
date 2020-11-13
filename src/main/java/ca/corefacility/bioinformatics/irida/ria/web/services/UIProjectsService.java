package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectInfoResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.Role;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ManageLocalProjectSettingsPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

/**
 * A utility class for formatting responses for the project members page UI.
 */
@Component
public class UIProjectsService {
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MessageSource messageSource;
	private final ProjectOwnerPermission projectOwnerPermission;
	private final ManageLocalProjectSettingsPermission projectMembersPermission;
	/*
	All roles that are available on a project.
	 */
	private final List<String> PROJECT_ROLES = ImmutableList.of("PROJECT_USER", "PROJECT_OWNER");

	@Autowired
	public UIProjectsService(ProjectService projectService, SampleService sampleService, MessageSource messageSource, ProjectOwnerPermission projectOwnerPermission, ManageLocalProjectSettingsPermission projectMembersPermission) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.messageSource = messageSource;
		this.projectOwnerPermission = projectOwnerPermission;
		this.projectMembersPermission = projectMembersPermission;
	}

	/**
	 * Get a list of all roles available on a project
	 *
	 * @param locale - {@link Locale}
	 * @return list of roles and their internationalized strings
	 */
	public List<Role> getProjectRoles(Locale locale) {
		return PROJECT_ROLES.stream()
				.map(role -> new Role(role, messageSource.getMessage("projectRole." + role, new Object[] {}, locale)))
				.collect(Collectors.toList());
	}

	/**
	 * Get the table contents for the projects listing table based on the user and table request.
	 *
	 * @param tableRequest - {@link TableRequest}
	 * @param isAdmin - whether this is the full administration table or a user listing.
	 * @return {@link TableResponse} with the current list of projects
	 */
	public TableResponse getPagedProjects(TableRequest tableRequest, Boolean isAdmin) {
		Page<Project> page = isAdmin ? getPagedProjectsForAdmin(tableRequest) : getPagedProjectsForUser(tableRequest);
		List<ProjectModel> projects = page.getContent()
				.stream()
				.map(p -> new ProjectModel(p, sampleService.getNumberOfSamplesForProject(p)))
				.collect(Collectors.toList());
		return new TableResponse(projects, page.getTotalElements());
	}

	/**
	 * Get the table contents for the projects listing table for an administrator based on the table request.
	 *
	 * @param tableRequest - {@link TableRequest}
	 * @return {@link TableResponse} with the current list of projects
	 */
	private Page<Project> getPagedProjectsForAdmin(TableRequest tableRequest) {
		return projectService.findAllProjects(tableRequest.getSearch(), tableRequest.getCurrent(),
				tableRequest.getPageSize(), tableRequest.getSort());
	}

	/**
	 * Get the table contents for the projects listing table for a user based on the table request.
	 *
	 * @param tableRequest - {@link TableRequest}
	 * @return {@link TableResponse} with the current list of projects
	 */
	private Page<Project> getPagedProjectsForUser(TableRequest tableRequest) {
		return projectService.findProjectsForUser(tableRequest.getSearch(), tableRequest.getCurrent(),
				tableRequest.getPageSize(), tableRequest.getSort());
	}

	/**
	 * Get information about a project as well as permissions
	 *
	 * @param projectId - The project to get info for
	 * @return {@link ProjectInfoResponse}
	 */
	public ProjectInfoResponse getProjectInfo(Long projectId) {
		Project project = projectService.read(projectId);

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		boolean isAdmin = user.getSystemRole()
				.equals(ca.corefacility.bioinformatics.irida.model.user.Role.ROLE_ADMIN);

		String projectName = project.getName();

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		boolean isOwner = projectOwnerPermission.isAllowed(authentication, project);

		boolean isOwnerAllowRemote = projectMembersPermission.isAllowed(authentication, project);

		return new ProjectInfoResponse(project.getId(), projectName, isAdmin || isOwner, isAdmin || isOwnerAllowRemote);
	}

}

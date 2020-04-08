package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.Role;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

@Component
public class ProjectsService {
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MessageSource messageSource;

	private final List<String> PROJECT_ROLES = ImmutableList.of("PROJECT_USER", "PROJECT_OWNER");

	@Autowired
	public ProjectsService(ProjectService projectService, SampleService sampleService, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.messageSource = messageSource;
	}

	public List<Role> getProjectRoles(Locale locale) {
		return PROJECT_ROLES.stream()
				.map(role -> new Role(role, messageSource.getMessage("projectRole." + role, new Object[] {}, locale)))
				.collect(Collectors.toList());
	}

	public TableResponse getPagedProjects(TableRequest tableRequest, Boolean isAdmin) {
		Page<Project> page = isAdmin ? getPagedProjectsForAdmin(tableRequest) : getPagedProjectsForUser(tableRequest);
		List<ProjectModel> projects = page.getContent()
				.stream()
				.map(p -> new ProjectModel(p, sampleService.getNumberOfSamplesForProject(p)))
				.collect(Collectors.toList());
		return new TableResponse(projects, page.getTotalElements());
	}

	private Page<Project> getPagedProjectsForAdmin(TableRequest tableRequest) {
		return projectService.findAllProjects(tableRequest.getSearch(), tableRequest.getCurrent(),
				tableRequest.getPageSize(), tableRequest.getSort());
	}

	private Page<Project> getPagedProjectsForUser(TableRequest tableRequest) {
		return projectService.findProjectsForUser(tableRequest.getSearch(), tableRequest.getCurrent(),
				tableRequest.getPageSize(), tableRequest.getSort());
	}
}

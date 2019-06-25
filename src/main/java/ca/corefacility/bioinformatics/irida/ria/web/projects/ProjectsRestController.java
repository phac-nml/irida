package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectsRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectsResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@RestController
@RequestMapping("/ajax/projects")
public class ProjectsRestController {
	private ProjectService projectService;
	private SampleService sampleService;

	@Autowired
	public ProjectsRestController(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	@RequestMapping
	public ProjectsResponse getPagedProjectsForUser(@RequestBody ProjectsRequest projectsRequest) {
		final Page<Project> page = projectService.findAllProjects("", projectsRequest.getCurrent(),
				projectsRequest.getPageSize(), projectsRequest.getSort());
		List<ProjectModel> projects = page.getContent()
				.stream()
				.map(p -> new ProjectModel(p, sampleService.getNumberOfSamplesForProject(p)))
				.collect(Collectors.toList());
		return new ProjectsResponse(projects, page.getTotalElements());
	}
}

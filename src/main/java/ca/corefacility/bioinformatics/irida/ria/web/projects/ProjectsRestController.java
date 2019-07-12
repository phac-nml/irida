package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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

/**
 * Controller for handling all ajax requests on the Projects listing page.
 */
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

	/**
	 * Handle request for get a filtered and sorted list of projects for a user or administrator
	 *
	 * @param projectsRequest {@link ProjectsRequest} Details about what is needed in the table (sort, filter, and search).
	 * @param request         {@link HttpServletRequest}
	 * @return {@link ProjectsResponse}
	 */
	@RequestMapping
	public ProjectsResponse getPagedProjectsForUser(@RequestBody ProjectsRequest projectsRequest, HttpServletRequest request) {
		boolean all = request.getHeader("referer")
				.endsWith("all");
		final Page<Project> page;
		if (all) {
			page = projectService.findAllProjects(projectsRequest.getSearch(), projectsRequest.getCurrent(),
					projectsRequest.getPageSize(), projectsRequest.getSort());
		} else {
			page = projectService.findProjectsForUser(projectsRequest.getSearch(), projectsRequest.getCurrent(),
					projectsRequest.getPageSize(), projectsRequest.getSort());
		}
		List<ProjectModel> projects = page.getContent()
				.stream()
				.map(p -> new ProjectModel(p, sampleService.getNumberOfSamplesForProject(p)))
				.collect(Collectors.toList());
		return new ProjectsResponse(projects, page.getTotalElements());
	}
}

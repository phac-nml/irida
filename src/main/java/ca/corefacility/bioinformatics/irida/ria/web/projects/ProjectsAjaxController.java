package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Controller for handling all ajax requests on the Projects listing page.
 */
@RestController
@RequestMapping("/ajax/projects")
public class ProjectsAjaxController {
	private final ProjectService projectService;
	private final SampleService sampleService;

	@Autowired
	public ProjectsAjaxController(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	/**
	 * Handle request for get a filtered and sorted list of projects for a user or administrator
	 *
	 * @param tableRequest {@link TableRequest} Details about what is needed in the table (sort, filter, and search).
	 * @param admin           {@link Boolean} Is the user on an administration page.
	 * @return {@link TableResponse}
	 */
	@RequestMapping
	public TableResponse getPagedProjectsForUser(@RequestBody TableRequest tableRequest,
			@RequestParam Boolean admin) {
		final Page<Project> page;
		if (admin) {
			page = projectService.findAllProjects(tableRequest.getSearch(), tableRequest.getCurrent(),
					tableRequest.getPageSize(), tableRequest.getSort());
		} else {
			page = projectService.findProjectsForUser(tableRequest.getSearch(), tableRequest.getCurrent(),
					tableRequest.getPageSize(), tableRequest.getSort());
		}
		List<ProjectModel> projects = page.getContent()
				.stream()
				.map(p -> new ProjectModel(p, sampleService.getNumberOfSamplesForProject(p)))
				.collect(Collectors.toList());
		return new TableResponse(projects, page.getTotalElements());
	}
}

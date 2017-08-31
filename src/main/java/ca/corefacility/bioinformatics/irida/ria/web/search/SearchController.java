package ca.corefacility.bioinformatics.irida.ria.web.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProject;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Controller
public class SearchController {
	private final ProjectService projectService;
	private final SampleService sampleService;

	@Autowired
	public SearchController(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	@RequestMapping("/search/projects")
	@ResponseBody
	public DataTablesResponse searchProjects(@RequestParam String query, @DataTablesRequest DataTablesParams params) {
		final Page<Project> page = projectService
				.findProjectsForUser(query, params.getCurrentPage(), params.getLength(),
						params.getSort());
		List<DataTablesResponseModel> projects = page.getContent().stream().map(this::createDataTablesProject).collect(Collectors.toList());
		return new DataTablesResponse(params, page, projects);
	}

	@RequestMapping("/search")
	public String search(@RequestParam String query, Model model) {
		model.addAttribute("searchQuery", query);

		return "search/search";
	}

	/**
	 * Extract the details of the a {@link Project} into a {@link DTProject}
	 * which is consumable by the UI
	 *
	 * @param project
	 *            {@link Project}
	 *
	 * @return {@link DTProject}
	 */
	private DTProject createDataTablesProject(Project project) {
		return new DTProject(project, sampleService.getNumberOfSamplesForProject(project));
	}
}

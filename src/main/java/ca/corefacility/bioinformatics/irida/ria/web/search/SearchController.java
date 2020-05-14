package ca.corefacility.bioinformatics.irida.ria.web.search;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProject;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProjectSamples;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.Lists;

/**
 * Controller to manage global searching
 */
@Controller
public class SearchController {
	private final ProjectService projectService;
	private final SampleService sampleService;

	@Autowired
	public SearchController(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	/**
	 * Search all projects a user is a member of based on a query string
	 * 
	 * @param query
	 *            the query string
	 * @param global
	 *            Whether to perform an admin global search
	 * @param params
	 *            parameters for a datatables response
	 * @return a {@link DataTablesResponse} to display the search results
	 */
	@RequestMapping("/search/ajax/projects")
	@ResponseBody
	public DataTablesResponse searchProjects(@RequestParam String query,
			@RequestParam(required = false, defaultValue = "false") boolean global,
			@DataTablesRequest DataTablesParams params) {
		Page<Project> page;
		if (global) {
			page = projectService.findAllProjects(query, params.getCurrentPage(), params.getLength(), params.getSort());
		} else {
			page = projectService.findProjectsForUser(query, params.getCurrentPage(), params.getLength(),
					params.getSort());
		}
		List<DataTablesResponseModel> projects = page.getContent().stream().map(this::createDataTablesProject)
				.collect(Collectors.toList());
		return new DataTablesResponse(params, page, projects);
	}

	/**
	 * Search all {@link Sample}s in projects for a user based on a query string
	 *
	 * @param query  the query string
	 * @param global Whether to perform an admin
	 *               global search
	 * @param params parameters for a datatables response
	 * @return a {@link DataTablesResponse} to display search results
	 */
	@RequestMapping("/search/ajax/samples")
	@ResponseBody
	public DataTablesResponse searchSamples(@RequestParam String query,
			@RequestParam(required = false, defaultValue = "false") boolean global,
			@DataTablesRequest DataTablesParams params) {

		Sort originalSort = params.getSort();
		List<Sort.Order> orders = Lists.newArrayList();
		originalSort.forEach(o -> {
			orders.add(new Sort.Order(o.getDirection(), "sample." + o.getProperty()));
		});

		Sort newSort = Sort.by(orders);
		Page<ProjectSampleJoin> samplePage;
		if (global) {
			samplePage = sampleService.searchAllSamples(query, params.getCurrentPage(), params.getLength(), newSort);
		} else {
			samplePage = sampleService.searchSamplesForUser(query, params.getCurrentPage(), params.getLength(),
					newSort);
		}

		List<DataTablesResponseModel> samples = samplePage.getContent().stream().map(this::createDataTablesSample)
				.collect(Collectors.toList());
		return new DataTablesResponse(params, samplePage, samples);
	}

	/**
	 * Get the search view with a given query
	 *
	 * @param query  the query string
	 * @param global Whether to perform an admin
	 *               global search
	 * @param model  model for the view
	 * @return name of the search view
	 */
	@RequestMapping("/search")
	public String search(@RequestParam String query,
			@RequestParam(required = false, defaultValue = "false") boolean global, Model model) {
		model.addAttribute("searchQuery", query);
		model.addAttribute("searchGlobal", global);

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

	/**
	 * Extract the details of a {@link ProjectSampleJoin} into a
	 * {@link DTProjectSamples}
	 * 
	 * @param join
	 *            the {@link ProjectSampleJoin}
	 * @return the created {@link DTProjectSamples}
	 */
	private DTProjectSamples createDataTablesSample(ProjectSampleJoin join) {
		return new DTProjectSamples(join, Lists.newArrayList(), null);
	}
}

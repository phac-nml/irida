package ca.corefacility.bioinformatics.irida.ria.web.search;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProject;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProjectSamples;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.search.dto.SearchItem;
import ca.corefacility.bioinformatics.irida.ria.web.search.dto.SearchProject;
import ca.corefacility.bioinformatics.irida.ria.web.search.dto.SearchRequest;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
	 * Get the search view
	 *
	 * @return name of the search view
	 */
	@RequestMapping("/search")
	public String search() {
		return "search/search";
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
	@PostMapping("/ajax/search/projects")
	public ResponseEntity<AntTableResponse<SearchItem>> handleSearch(@RequestBody SearchRequest request) {
		Page<Project> page;
		if (request.isGlobal()) {
			page = projectService.findAllProjects((String) request.getSearch().get(0).getValue(), request.getPage(), request.getPageSize(), request.getSort());
		} else {
			page = projectService.findProjectsForUser((String) request.getSearch().get(0).getValue(), request.getPage(), request.getPageSize(), request.getSort());
		}
		AntTableResponse<SearchItem> response = new AntTableResponse<>(page.getContent().stream().map(project -> {
			Long samples = sampleService.getNumberOfSamplesForProject(project);
			return new SearchProject(project, samples);
		}).collect(Collectors.toList()), page.getTotalElements());
		return ResponseEntity.ok(response);
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
	@RequestMapping("/ajax/search/samples")
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

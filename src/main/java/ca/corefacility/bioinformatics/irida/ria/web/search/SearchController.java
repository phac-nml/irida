package ca.corefacility.bioinformatics.irida.ria.web.search;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProject;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTProjectSamples;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.search.dto.SearchItem;
import ca.corefacility.bioinformatics.irida.ria.web.search.dto.SearchProject;
import ca.corefacility.bioinformatics.irida.ria.web.search.dto.SearchRequest;
import ca.corefacility.bioinformatics.irida.ria.web.search.dto.SearchSample;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * @param query  the query string
     * @param global Whether to perform an admin global search
     * @param params parameters for a datatables response
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
    public ResponseEntity<AntTableResponse<SearchItem>> searchSamples(@RequestBody SearchRequest request) {

        Sort originalSort = request.getSort();
        List<Sort.Order> orders = Lists.newArrayList();
        originalSort.forEach(o -> {
            orders.add(new Sort.Order(o.getDirection(), "sample." + o.getProperty()));
        });

        Sort newSort = Sort.by(orders);
        Page<ProjectSampleJoin> samplePage;
        if (request.isGlobal()) {
            samplePage = sampleService.searchAllSamples((String) request.getSearch().get(0).getValue(), request.getPage(), request.getPageSize(), newSort);
        } else {
            samplePage = sampleService.searchSamplesForUser((String) request.getSearch().get(0).getValue(), request.getPage(), request.getPageSize(),
                    newSort);
        }

        List<SearchItem> samples = samplePage.getContent().stream().map(join -> {
            Sample sample = join.getObject();
            List<Join<Project, Sample>> projects = projectService.getProjectsForSample(sample);
            List<SearchProject> searchProjects = projects.stream().map(projectSampleJoin -> new SearchProject(projectSampleJoin.getSubject(), 0L)).collect(Collectors.toList());
            return new SearchSample(sample, searchProjects);
        }).collect(Collectors.toList());
        AntTableResponse<SearchItem> response = new AntTableResponse<>(samples, samplePage.getTotalElements());
        return ResponseEntity.ok(response);
    }

    /**
     * Extract the details of the a {@link Project} into a {@link DTProject}
     * which is consumable by the UI
     *
     * @param project {@link Project}
     * @return {@link DTProject}
     */
    private DTProject createDataTablesProject(Project project) {
        return new DTProject(project, sampleService.getNumberOfSamplesForProject(project));
    }

    /**
     * Extract the details of a {@link ProjectSampleJoin} into a
     * {@link DTProjectSamples}
     *
     * @param join the {@link ProjectSampleJoin}
     * @return the created {@link DTProjectSamples}
     */
    private DTProjectSamples createDataTablesSample(ProjectSampleJoin join) {
        return new DTProjectSamples(join, Lists.newArrayList(), null);
    }
}

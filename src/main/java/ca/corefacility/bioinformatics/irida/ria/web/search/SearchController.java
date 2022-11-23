package ca.corefacility.bioinformatics.irida.ria.web.search;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
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
     * @param request Details about the search request
     * @return Paged list of projects and the total count found for the search
     */
    @RequestMapping("/ajax/search/projects")
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
     * @param request Details about the search request
     * @return Paged list of samples and the total count found in the search
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
}

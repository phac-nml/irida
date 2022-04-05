package ca.corefacility.bioinformatics.irida.ria.web.projects;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectCartSample;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.MergeRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.error.SampleMergeException;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UI Ajax Controller for the project samples page.
 */
@RestController
@RequestMapping("/ajax/project-samples/{projectId}")
public class AjaxSamplesController {
    private final UISampleService uiSampleService;

    @Autowired
    public AjaxSamplesController(UISampleService uiSampleService) {
        this.uiSampleService = uiSampleService;
    }

    /**
     * Returns a Page of samples for a project based on the information in the {@link ProjectSamplesTableRequest}
     *
     * @param projectId Identifier for the current project
     * @param request   Information about the current state of the project samples table.
     * @return The Page of samples
     */
    @PostMapping("")
    public ResponseEntity<AntTableResponse<ProjectSampleTableItem>> getPagedProjectSamples(@PathVariable Long projectId, @RequestBody ProjectSamplesTableRequest request) {
        return ResponseEntity.ok(uiSampleService.getPagedProjectSamples(projectId, request));
    }

    @PostMapping("/ids")
    public ResponseEntity<List<ProjectCartSample>> getProjectSamplesIds(@PathVariable Long projectId, @RequestBody ProjectSamplesTableRequest request) {
        return ResponseEntity.ok(uiSampleService.getFilteredProjectSamples(projectId, request));
    }

    @PutMapping("/merge")
    public ResponseEntity<AjaxResponse> mergeSamples(@PathVariable Long projectId, @RequestBody MergeRequest request) {
        try {
            uiSampleService.mergeSamples(projectId, request);
            return ResponseEntity.ok(new AjaxSuccessResponse(""));
        } catch (SampleMergeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxErrorResponse(e.getMessage()));
        }
    }
}

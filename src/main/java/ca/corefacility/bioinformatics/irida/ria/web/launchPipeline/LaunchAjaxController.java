package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.pipelines.ReferenceFileRequiredException;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.CreateNamedParameterSetAjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.SavePipelineParametersRequest;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.LaunchRequest;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.LaunchSample;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineSampleService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineStartService;

/**
 * Controller to handle AJAX requests from the UI for Workflow Pipelines
 */
@RestController
@RequestMapping("/ajax/pipeline")
public class LaunchAjaxController {
    private final UIPipelineService pipelineService;
    private final UIPipelineStartService startService;
    private final UIPipelineSampleService sampleService;

    @Autowired
    public LaunchAjaxController(UIPipelineService pipelineService, UIPipelineStartService pipelineStartService,
            UIPipelineSampleService sampleService) {
        this.pipelineService = pipelineService;
        this.startService = pipelineStartService;
        this.sampleService = sampleService;
    }

    /**
     * Get the launch page for a specific IRIDA Workflow Pipeline.
     *
     * @param id     identifier for a pipeline.
     * @param locale current users locale information
     * @return The details about a specific pipeline else returns a status that the pipeline cannot be found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AjaxResponse> getPipelineDetails(@PathVariable UUID id, Locale locale) {
        try {
            return ResponseEntity.ok(pipelineService.getPipelineDetails(id, locale));
        } catch (IridaWorkflowException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AjaxErrorResponse("Cannot find this pipeline"));
        }
    }

    /**
     * Get a list of the samples that are in the cart and get their associated sequence files that
     * can be used on the current pipeline
     *
     * @param paired  Whether paired end files can be run on the current pipeline
     * @param singles Whether single end files can be run on the current pipeline
     * @return list of samples containing their associated sequencing data
     */
    @GetMapping("/samples")
    public ResponseEntity<List<LaunchSample>> getPipelineSamples(
            @RequestParam(required = false, defaultValue = "false") boolean paired,
            @RequestParam(required = false, defaultValue = "false") boolean singles) {
        return ResponseEntity.ok(sampleService.getPipelineSamples(paired, singles));
    }

    /**
     * Launch a new IRIDA Workflow Pipeline
     *
     * @param id The UUID for a workflow
     * @param request required parameters to launch the pipeline
     * @param locale the Locale of the currently logged in user.
     * @return A response to let the UI know the pipeline was launched successfully
     */
    @PostMapping("/{id}")
    public ResponseEntity<AjaxResponse> launchPipeline(@PathVariable UUID id, @RequestBody LaunchRequest request, Locale locale) {
        try {
            return ResponseEntity.ok(new AjaxCreateItemSuccessResponse(startService.start(id, request, locale)));
        } catch (IridaWorkflowNotFoundException | ReferenceFileRequiredException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AjaxErrorResponse(e.getMessage()));

        }
    }

    /**
     * Save a new set of named pipeline parameters
     *
     * @param id         identifier for a irida workflow
     * @param parameters details about the new set of parameters
     * @return The identifier for the newly created named parameter set, wrapped in a ajax response
     */
    @PostMapping("/{id}/parameters")
    public ResponseEntity<AjaxResponse> saveNewPipelineParameters(@PathVariable UUID id,
            @RequestBody SavePipelineParametersRequest parameters, Locale locale) {
        try {
            return ResponseEntity.ok(new CreateNamedParameterSetAjaxResponse(pipelineService.saveNewPipelineParameters(id, parameters, locale)));
        } catch (IridaWorkflowNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AjaxErrorResponse("Pipeline cannot be found"));
        }
    }
}

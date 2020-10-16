package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.UUID;

/**
 * Controller to handle AJAX requests from the UI for Workflow Pipelines
 */
@RestController
@RequestMapping("/ajax/pipeline")
public class PipelineAjaxController {
    private final UIPipelineService pipelineService;

    @Autowired
    public PipelineAjaxController(UIPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AjaxResponse> getPipelineDetails(@PathVariable UUID id, Locale locale) {
        try {
            return ResponseEntity.ok(pipelineService.getPipelineDetails(id, locale));
        } catch (IridaWorkflowException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AjaxErrorResponse("Cannot find this pipeline"));
        }
    }
}

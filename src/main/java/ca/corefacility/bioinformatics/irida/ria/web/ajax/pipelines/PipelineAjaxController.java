package ca.corefacility.bioinformatics.irida.ria.web.ajax.pipelines;

import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines.PipelineLaunchDetails;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines.PipelineParametersSaveRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineService;

@RestController
@RequestMapping("/ajax/pipelines")
@Scope("session")
public class PipelineAjaxController {
	private final UIPipelineService service;

	@Autowired
	public PipelineAjaxController(UIPipelineService service) {
		this.service = service;
	}

	@GetMapping("/{workflowId}")
	public ResponseEntity<UIPipelineDetailsResponse> getPipelineDetails(@PathVariable UUID workflowId, Locale locale) {
		try {
			return ResponseEntity.ok(service.getPipelineDetails(workflowId, locale));
		} catch (IridaWorkflowNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(null);
		}
	}

	@PostMapping("/{workflowId}")
	public ResponseEntity<String> launchPipeline(@PathVariable UUID workflowId,
			@RequestBody PipelineLaunchDetails details) {
		return ResponseEntity.ok("");
	}

	@PostMapping("/{workflowId}/parameters")
	public ResponseEntity<Long> savePipelineParameters(@PathVariable UUID workflowId, @RequestBody PipelineParametersSaveRequest request) {
		return ResponseEntity.ok(service.savePipelineParameters(workflowId, request));
	}
}

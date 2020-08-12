package ca.corefacility.bioinformatics.irida.ria.web.ajax.pipelines;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
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

	@RequestMapping("/{workflowId}")
	public ResponseEntity<UIPipelineDetailsResponse> getPipelineDetails(@PathVariable UUID workflowId, @RequestParam boolean automated) {
		try {
			return ResponseEntity.ok(service.getPipelineDetails(workflowId, automated));
		} catch (IridaWorkflowNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(null);
		}
	}
}

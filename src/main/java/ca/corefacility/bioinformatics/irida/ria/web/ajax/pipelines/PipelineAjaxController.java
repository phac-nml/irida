package ca.corefacility.bioinformatics.irida.ria.web.ajax.pipelines;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ajax/pipelines")
public class PipelineAjaxController {

	@RequestMapping("/{workflowId}")
	public ResponseEntity<String> getPipelineDetails(@PathVariable String workflowId) {
		return ResponseEntity.ok("");
	}
}

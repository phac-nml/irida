package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateSampleResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSampleService;

@RestController
@RequestMapping("/ajax/projects/{projectId}/samples")
public class ProjectSamplesAjaxController {
	private final UIProjectSampleService service;

	@Autowired
	public ProjectSamplesAjaxController(UIProjectSampleService service) {
		this.service = service;
	}

	@RequestMapping("/add-sample/validate")
	public ResponseEntity<SampleNameValidationResponse> validateNewSampleName(@RequestBody SampleNameValidationRequest request, @PathVariable long projectId) {
		return service.validateNewSampleName(request, projectId);
	}

	@PostMapping("/add-sample")
	public ResponseEntity<CreateSampleResponse> createSampleInProject(@RequestBody CreateSampleRequest request,
			@PathVariable long projectId) {
		return service.createSample(request, projectId);
	}
}

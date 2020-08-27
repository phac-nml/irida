package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSampleService;

/**
 * Ajax Controller for handling asynchronous requests for project samples.
 */
@RestController
@RequestMapping("/ajax/projects/{projectId}/samples")
public class ProjectSamplesAjaxController {
	private final UIProjectSampleService service;

	@Autowired
	public ProjectSamplesAjaxController(UIProjectSampleService service) {
		this.service = service;
	}

	/**
	 * Ensure a potential sample name meets criteria
	 *
	 * @param name      to evaluate
	 * @param projectId current project identifier
	 * @param locale    current users locale
	 * @return result of validating the name.  Error status returned if name does not meet criteria.
	 */
	@RequestMapping("/add-sample/validate")
	public ResponseEntity<SampleNameValidationResponse> validateNewSampleName(@RequestParam String name,
			@PathVariable long projectId, Locale locale) {
		return service.validateNewSampleName(name, projectId, locale);
	}

	/**
	 * Create a new sample within a project
	 *
	 * @param request   Details about the sample - name and organism
	 * @param projectId current project identifier
	 * @param locale    current users locale
	 * @return result of creating the project
	 */
	@PostMapping("/add-sample")
	public ResponseEntity<AjaxResponse> createSampleInProject(@RequestBody CreateSampleRequest request,
			@PathVariable long projectId, Locale locale) {
		return service.createSample(request, projectId, locale);
	}
}

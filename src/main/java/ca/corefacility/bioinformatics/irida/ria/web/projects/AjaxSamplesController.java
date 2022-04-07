package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<AntTableResponse<ProjectSampleTableItem>> getPagedProjectSamples(@PathVariable Long projectId,
			@RequestBody ProjectSamplesTableRequest request) {
		return ResponseEntity.ok(uiSampleService.getPagedProjectSamples(projectId, request));
	}

	/**
	 * Get a list of all samples in the current project and associated project that have been filtered, return a minimal
	 * representation of them.
	 *
	 * @param projectId Identifier for the current project
	 * @param request   Details about the state of the filters
	 * @return list of minimal samples
	 */
	@PostMapping("/ids")
	public ResponseEntity<List<ProjectCartSample>> getMinimalSampleDetailsForFilteredProject(
			@PathVariable Long projectId, @RequestBody ProjectSamplesTableRequest request) {
		return ResponseEntity.ok(uiSampleService.getMinimalSampleDetailsForFilteredProject(projectId, request));
	}

	/**
	 * Merge 1 or more samples into another sample.
	 *
	 * @param projectId Identifier for the current project
	 * @param request   All information about the samples to merge
	 * @param locale    current users locale information
	 * @return result of the merge
	 */
	@PostMapping("/merge")
	public ResponseEntity<AjaxResponse> mergeSamples(@PathVariable Long projectId, @RequestBody MergeRequest request,
			Locale locale) {
		try {
			String response = uiSampleService.mergeSamples(projectId, request, locale);
			return ResponseEntity.ok(new AjaxSuccessResponse(response));
		} catch (SampleMergeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxErrorResponse(e.getMessage()));
		}
	}
}

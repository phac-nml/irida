package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.DownloadRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectCartSample;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.MergeRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.RemoveSamplesRequest;
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
			@RequestBody ProjectSamplesTableRequest request, Locale locale) {
		return ResponseEntity.ok(uiSampleService.getPagedProjectSamples(projectId, request, locale));
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

	/**
	 * Remove 1 or more samples from a project.
	 *
	 * @param projectId Identifier for the project
	 * @param request   All information about the samples to remove
	 * @return result of the removal
	 */
	@DeleteMapping("/remove")
	public ResponseEntity<AjaxResponse> removeSamplesFromProject(@PathVariable long projectId,
			@RequestBody RemoveSamplesRequest request) {
		String result = uiSampleService.removeSamplesFromProject(projectId, request.getSampleIds());
		return ResponseEntity.ok(new AjaxSuccessResponse(result));
	}

	/**
	 * Download a zipped file containing sequencing reads for a list of samples.
	 *
	 * @param projectId Identifier for a project
	 * @param request   Details about the download request including a list of sample identifiers
	 * @param response  {@link HttpServletResponse}
	 * @return Zipped file of sequence files
	 * @throws IOException thrown if error reading files.
	 */
	@PostMapping("/download")
	public ResponseEntity<StreamingResponseBody> downloadSamples(@PathVariable long projectId,
			@RequestBody DownloadRequest request, HttpServletResponse response) throws IOException {
		return ResponseEntity.ok(uiSampleService.downloadSamples(projectId, request.getSampleIds(), response));
	}

	@PostMapping("/export")
	public ResponseEntity<StreamingResponseBody> downloadSamplesSpreadsheet(@PathVariable long projectId, @RequestParam String type,
			@RequestBody ProjectSamplesTableRequest request, HttpServletResponse response, Locale locale) {
		return ResponseEntity.ok(
				uiSampleService.downloadSamplesSpreadsheet(projectId, type, request, response, locale));
	}
}

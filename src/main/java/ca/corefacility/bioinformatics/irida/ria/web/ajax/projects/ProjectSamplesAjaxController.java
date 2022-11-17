package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleFilesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.SampleNameValidationResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UpdateSampleRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto.ValidateSampleNamesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIShareSamplesException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.DownloadRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectCartSample;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.MergeRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples.RemoveSamplesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.error.SampleMergeException;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareSamplesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSampleService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;

/**
 * AJAX Controller for handling asynchronous requests for project samples.
 */
@RestController
@RequestMapping("/ajax/projects/{projectId}/samples")
public class ProjectSamplesAjaxController {
	private final UIProjectSampleService uiProjectSampleService;
	private final UISampleService uiSampleService;

	@Autowired
	public ProjectSamplesAjaxController(UIProjectSampleService uiProjectSampleService,
			UISampleService uiSampleService) {
		this.uiProjectSampleService = uiProjectSampleService;
		this.uiSampleService = uiSampleService;
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
		return uiProjectSampleService.validateNewSampleName(name, projectId, locale);
	}

	/**
	 * Create a new sample within a project
	 *
	 * @param request   Details about the sample
	 * @param projectId current project identifier
	 * @param locale    current users locale
	 * @return result of creating the project
	 */
	@PostMapping("/add-sample")
	public ResponseEntity<AjaxResponse> createSampleInProject(@RequestBody CreateSampleRequest request,
			@PathVariable long projectId, Locale locale) {
		return uiProjectSampleService.createSample(request, projectId, locale);
	}

	/**
	 * Update a sample within a project
	 *
	 * @param request  Details about the sample
	 * @param sampleId sample identifier
	 * @param locale   current users locale
	 * @return result of creating the project
	 */
	@PatchMapping("/add-sample/{sampleId}")
	public ResponseEntity<AjaxResponse> updateSampleInProject(@RequestBody UpdateSampleRequest request,
			@PathVariable Long projectId, @PathVariable long sampleId, Locale locale) {
		return uiProjectSampleService.updateSample(request, sampleId, locale);
	}

	/**
	 * Returns a Page of samples for a project based on the information in the {@link ProjectSamplesTableRequest}
	 *
	 * @param projectId Identifier for the current project
	 * @param request   Information about the current state of the project samples table.
	 * @param locale    Current users locale
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
		uiSampleService.removeSamplesFromProject(projectId, request.getSampleIds());
		return ResponseEntity.ok(new AjaxSuccessResponse(""));
	}

	/**
	 * Download a zipped file containing sequencing reads for a list of samples.
	 *
	 * @param projectId Identifier for a project
	 * @param request   Details about the download request including a list of sample identifiers
	 * @param response  {@link HttpServletResponse}
	 * @return Zipped file of sequence files
	 */
	@PostMapping("/download")
	public ResponseEntity<StreamingResponseBody> downloadSamples(@PathVariable long projectId,
			@RequestBody DownloadRequest request, HttpServletResponse response) {
		return ResponseEntity.ok(uiSampleService.downloadSamples(projectId, request.getSampleIds(), response));
	}

	/**
	 * Export the current state of the Project Samples table as either a CSV or Excel file.
	 *
	 * @param projectId Identifier for the current project
	 * @param type      Type of file to export (CSV or Excel)
	 * @param request   Current state of the samples table
	 * @param response  {@link HttpServletResponse}
	 * @param locale    current users {@link Locale}
	 * @throws IOException Thrown if issue export the file
	 */
	@PostMapping("/export")
	public void downloadSamplesSpreadsheet(@PathVariable long projectId, @RequestParam String type,
			@RequestBody ProjectSamplesTableRequest request, HttpServletResponse response, Locale locale)
			throws IOException {
		uiSampleService.downloadSamplesSpreadsheet(projectId, type, request, response, locale);
	}

	/**
	 * Share / Move samples between projects
	 *
	 * @param request {@link ShareSamplesRequest} details about the samples to share
	 * @param locale  current users {@link Locale}
	 * @return Outcome of the share/move
	 */
	@PostMapping("/share")
	public ResponseEntity<AjaxResponse> shareSamplesWithProject(@RequestBody ShareSamplesRequest request,
			Locale locale) {
		try {
			uiSampleService.shareSamplesWithProject(request, locale);
			return ResponseEntity.ok(new AjaxSuccessResponse(""));
		} catch (UIShareSamplesException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AjaxErrorResponse(e.getLocalizedMessage()));
		}
	}

	/**
	 * Get the set of files for samples by the sample identifiers
	 *
	 * @param ids       List of identifiers for the samples to get files for.
	 * @param projectId The project the samples belong to
	 * @return {@link SampleFilesResponse} a map of sample identifier and their corresponding files
	 */
	@GetMapping("/files")
	public SampleFilesResponse getFilesForSamples(@RequestParam List<Long> ids, @PathVariable Long projectId) {
		return uiSampleService.getFilesForSamples(ids, projectId);
	}

	/**
	 * Validate a list of samples names
	 *
	 * @param projectId project identifier
	 * @param request   {@link ValidateSampleNamesRequest} details about the sample names to validate
	 * @return a list of validated sample names
	 */
	@PostMapping("/validate")
	public ResponseEntity<AjaxResponse> validateSampleNames(@PathVariable Long projectId,
			@RequestBody ValidateSampleNamesRequest request) {
		return ResponseEntity.ok(uiProjectSampleService.validateSampleNames(projectId, request));
	}

}

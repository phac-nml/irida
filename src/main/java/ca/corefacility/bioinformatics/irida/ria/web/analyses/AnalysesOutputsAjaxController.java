package ca.corefacility.bioinformatics.irida.ria.web.analyses;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnalysesOutputsService;

/**
 * Controller for handling all ajax requests for Single Sample Analyses Outputs.
 */
@RestController
@Scope("session")
@RequestMapping("/ajax/analyses-outputs")
public class AnalysesOutputsAjaxController {
	private static final Logger logger = LoggerFactory.getLogger(AnalysesOutputsAjaxController.class);
	private UIAnalysesOutputsService uiAnalysesOutputsService;
	private MessageSource messageSource;

	@Autowired
	public AnalysesOutputsAjaxController(UIAnalysesOutputsService uiProjectAnalysesService, MessageSource messageSource) {
		this.uiAnalysesOutputsService = uiProjectAnalysesService;
		this.messageSource = messageSource;
	}

	/**
	 * Get all the shared single sample analysis outputs for the project
	 *
	 * @param projectId {@link ca.corefacility.bioinformatics.irida.model.project.Project} id
	 * @return a response containing a list of filtered {@link ProjectSampleAnalysisOutputInfo} shared single sample analysis outputs
	 */
	@GetMapping("/shared")
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getSharedSingleSampleOutputs(
			@RequestParam Long projectId) {
		return ResponseEntity.ok(uiAnalysesOutputsService.getSharedSingleSampleOutputs(projectId));
	}

	/**
	 * Get all the automated single sample analysis outputs for the project
	 *
	 * @param projectId {@link ca.corefacility.bioinformatics.irida.model.project.Project} id
	 * @return a response containing a list of filtered {@link ProjectSampleAnalysisOutputInfo} automated single sample analysis outputs
	 */
	@GetMapping("/automated")
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getAutomatedSingleSampleOutputs(
			@RequestParam Long projectId) {
		return ResponseEntity.ok(uiAnalysesOutputsService.getAutomatedSingleSampleOutputs(projectId));
	}

	/**
	 * Get all the user single sample analysis outputs
	 *
	 * @return a response containing a list of filtered {@link ProjectSampleAnalysisOutputInfo} user single sample analysis outputs
	 */
	@GetMapping(value = "/user")
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getAllUserAnalysisOutputInfo() {
		return ResponseEntity.ok(uiAnalysesOutputsService.getUserSingleSampleOutputs());
	}

	/**
	 * Prepare the download of multiple {@link AnalysisOutputFile} by adding them to a selection.
	 *
	 * @param locale  User's locale
	 * @param outputs Info for {@link AnalysisOutputFile} to download
	 */
	@PostMapping(value = "/download/prepare")
	public void prepareDownload(@RequestBody List<ProjectSampleAnalysisOutputInfo> outputs, Locale locale) {
		try {
			uiAnalysesOutputsService.prepareAnalysisOutputsSelectionDownload(outputs);
			ResponseEntity.status(HttpStatus.CREATED);
		} catch (Exception e) {
			String errorMessage = messageSource.getMessage("server.SingleSampleAnalysisOutputs.prepare.error", new Object[] {},
					locale);
			logger.error(errorMessage, e);
			ResponseEntity.status(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Download the selected {@link AnalysisOutputFile}.
	 *
	 * @param filename Optional filename for file download.
	 * @param response {@link HttpServletResponse}
	 */
	@GetMapping(value = "/download/selection", produces = MediaType.APPLICATION_JSON_VALUE)
	public void downloadSelection(
			@RequestParam(required = false, defaultValue = "analysis-output-files-batch-download") String filename,
			HttpServletResponse response) {
		uiAnalysesOutputsService.downloadAnalysisOutputsSelection(filename, response);
	}

	/**
	 * Download single output files from an {@link AnalysisSubmission}
	 *
	 * @param analysisSubmissionId Id for a {@link AnalysisSubmission}
	 * @param fileId               the id of the file to download
	 * @param filename             Optional filename for file download.
	 * @param response             {@link HttpServletResponse}
	 */
	@GetMapping(value = "/download/file")
	public void downloadIndividualFile(@RequestParam Long analysisSubmissionId, @RequestParam Long fileId,
			@RequestParam(defaultValue = "", required = false) String filename, HttpServletResponse response) {
		uiAnalysesOutputsService.downloadIndividualAnalysisOutputFile(analysisSubmissionId, fileId, filename, response);
	}
}

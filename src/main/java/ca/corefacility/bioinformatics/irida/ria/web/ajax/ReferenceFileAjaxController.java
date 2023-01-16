package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxUpdateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.references.UIReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.references.UploadReferenceFilesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectReferenceFileService;

/**
 * Controller for ajax actions for reference files
 */

@RestController
@RequestMapping("/ajax/reference-files")
public class ReferenceFileAjaxController {
	private static final Logger logger = LoggerFactory.getLogger(ReferenceFileAjaxController.class);
	private final UIProjectReferenceFileService uiProjectReferenceFileService;

	@Autowired
	public ReferenceFileAjaxController(UIProjectReferenceFileService uiProjectReferenceFileService) {
		this.uiProjectReferenceFileService = uiProjectReferenceFileService;
	}

	/**
	 * Add a new reference file to a project.
	 *
	 * @param projectId The id of the project to add the file to.
	 * @param files     {@link List} of {@link MultipartFile} file being uploaded.
	 * @param locale    locale of the logged in user
	 * @return Success message if file was successfully uploaded
	 */
	@PostMapping("")
	public ResponseEntity<AjaxResponse> addReferenceFileToProject(@RequestParam(required = false) Long projectId,
			@RequestParam(value = "file") List<MultipartFile> files, final Locale locale) {
		try {
			return ResponseEntity.ok(new UploadReferenceFilesResponse(
					uiProjectReferenceFileService.addReferenceFileToProject(projectId, files, locale)));
		} catch (UnsupportedReferenceFileContentError | IOException e) {
			return ResponseEntity.status(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Delete a reference file. This will remove it from the project.
	 *
	 * @param fileId    The id of the file to remove.
	 * @param projectId the project to delete the reference file for.
	 * @param locale    the locale specified by the browser.
	 * @return Success or error based on the result of deleting the file.
	 */
	@DeleteMapping("")
	public ResponseEntity<AjaxResponse> deleteReferenceFile(@RequestParam(value = "fileId") Long fileId,
			@RequestParam(value = "projectId") Long projectId, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxUpdateItemSuccessResponse(
					uiProjectReferenceFileService.deleteReferenceFile(fileId, projectId, locale)));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Download a reference file based on the id passed.
	 *
	 * @param fileId   The id of the file to download
	 * @param response {@link HttpServletResponse} to write to file to
	 */
	@RequestMapping(value = "/download/{fileId}")
	public void downloadReferenceFile(@PathVariable Long fileId, HttpServletResponse response) {
		try {
			uiProjectReferenceFileService.downloadReferenceFile(fileId, response);
		} catch (IOException e) {
			logger.error("Unable to read file to download", e);
		}
	}

	/**
	 * Get the reference files for a project
	 *
	 * @param projectId the ID of the project
	 * @param locale    locale of the logged in user
	 * @return information about the reference files in the project
	 */
	@GetMapping("/{projectId}")
	public ResponseEntity<List<UIReferenceFile>> getReferenceFilesForProject(@PathVariable Long projectId,
			Locale locale) {
		return ResponseEntity.ok(uiProjectReferenceFileService.getReferenceFilesForProject(projectId, locale));
	}

}

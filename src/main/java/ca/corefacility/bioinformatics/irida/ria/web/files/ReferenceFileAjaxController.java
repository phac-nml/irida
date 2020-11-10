package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.*;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

@RestController
@RequestMapping("ajax/referenceFiles")
public class ReferenceFileAjaxController {

	private static final Logger logger = LoggerFactory.getLogger(ReferenceFileAjaxController.class);

	private final ProjectService projectService;
	private final ReferenceFileService referenceFileService;
	private final MessageSource messageSource;

	@Autowired
	public ReferenceFileAjaxController(ProjectService projectService, ReferenceFileService referenceFileService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.referenceFileService = referenceFileService;
		this.messageSource = messageSource;
	}

	/**
	 * Add a new reference file to a project.
	 *
	 * @param projectId The id of the project to add the file to.
	 * @param files     {@link List} of {@link MultipartFile} file being uploaded.
	 * @param locale    locale of the logged in user
	 * @return Success message if file was successfully uploaded
	 */
	@PostMapping("/project/{projectId}/new")
	public ResponseEntity<AjaxResponse> addReferenceFileToProject(@PathVariable Long projectId,
			@RequestParam(value = "file") List<MultipartFile> files, final Locale locale) {
		Project project = projectService.read(projectId);
		logger.debug("Adding reference file to project " + projectId);

		try {
			for (MultipartFile file : files) {
				// Prepare a new reference file using the multipart file supplied by the caller
				Path temp = Files.createTempDirectory(null);

				Path target = temp.resolve(file.getOriginalFilename());
				file.transferTo(target.toFile());

				ReferenceFile referenceFile = new ReferenceFile(target);
				projectService.addReferenceFileToProject(project, referenceFile);

				// Clean up temporary files
				Files.deleteIfExists(target);
				Files.deleteIfExists(temp);
			}
		} catch (final UnsupportedReferenceFileContentError e) {
			logger.error("User uploaded a reference file that biojava couldn't parse as DNA.", e);
			return ResponseEntity.status(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE)
					.body(new AjaxErrorResponse(
							messageSource.getMessage("server.projects.reference-file.invalid-content", new Object[] {},
									locale)));
		} catch (IOException e) {
			logger.error("Error writing sequence file", e);
			return ResponseEntity.status(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE)
					.body(new AjaxErrorResponse(
							messageSource.getMessage("server.projects.reference-file.unknown-error", new Object[] {},
									locale)));
		}

		// this isn't ever actually parsed by the page that's calling this
		// method so doesn't need to be i18n.
		return ResponseEntity.ok(new AjaxSuccessResponse("Upload complete"));
	}

	/**
	 * Delete a reference file. This will remove it from the project.
	 *
	 * @param fileId    The id of the file to remove.
	 * @param projectId the project to delete the reference file for.
	 * @param locale    the locale specified by the browser.
	 * @return Success or error based on the result of deleting the file.
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<AjaxResponse> deleteReferenceFile(@RequestParam(value = "fileId") Long fileId,
			@RequestParam(value = "projectId") Long projectId, Locale locale) {
		Project project = projectService.read(projectId);
		ReferenceFile file = referenceFileService.read(fileId);
		try {
			logger.info("Removing file with id of : " + fileId);
			projectService.removeReferenceFileFromProject(project, file);
			return ResponseEntity.ok(new AjaxRemoveItemSuccessResponse(
					messageSource.getMessage("server.projects.reference-file.delete-success",
							new Object[] { file.getLabel(), project.getName() }, locale)));
		} catch (EntityNotFoundException e) {
			logger.error("Failed to remove reference file, reason unknown.", e);
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
					.body(new AjaxErrorResponse(messageSource.getMessage("server.projects.reference-file.delete-error",
							new Object[] { file.getLabel(), project.getName() }, locale)));
		}
	}
}

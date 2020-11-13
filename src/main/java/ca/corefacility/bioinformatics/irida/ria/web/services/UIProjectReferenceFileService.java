package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxUpdateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * A utility class for formatting responses for the project reference files page UI.
 */

@Component
public class UIProjectReferenceFileService {
	private static final Logger logger = LoggerFactory.getLogger(UIProjectReferenceFileService.class);

	private final ProjectService projectService;
	private final ReferenceFileService referenceFileService;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectReferenceFileService(ProjectService projectService, ReferenceFileService referenceFileService, MessageSource messageSource) {
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
	 * @return Return success message or error if file was successfully uploaded or not
	 * @throws UnsupportedReferenceFileContentError if content is invalid
	 * @throws IOException if there is an I/O error
	 */
	public String addReferenceFileToProject(Long projectId, List<MultipartFile> files, final Locale locale) throws UnsupportedReferenceFileContentError, IOException {
		try {
			for (MultipartFile file : files) {
				// Prepare a new reference file using the multipart file supplied by the caller
				Path temp = Files.createTempDirectory(null);

				Path target = temp.resolve(file.getOriginalFilename());
				file.transferTo(target.toFile());

				ReferenceFile referenceFile = new ReferenceFile(target);
				if (projectId != null) {
					logger.debug("Adding reference file to project " + projectId);
					Project project = projectService.read(projectId);
					projectService.addReferenceFileToProject(project, referenceFile);
				}

				// Clean up temporary files
				Files.deleteIfExists(target);
				Files.deleteIfExists(temp);
			}
		} catch (UnsupportedReferenceFileContentError e) {
			logger.error("User uploaded a reference file that biojava couldn't parse as DNA.", e);
			throw new UnsupportedReferenceFileContentError(messageSource.getMessage("server.projects.reference-file.invalid-content", new Object[] {},
					locale), e);
		} catch (IOException e) {
			logger.error("Error writing sequence file", e);
			throw new UnsupportedReferenceFileContentError(messageSource.getMessage("server.projects.reference-file.unknown-error", new Object[] {}, locale), null);
		}

		return "Upload complete";
	}

	/**
	 * Delete a reference file. This will remove it from the project.
	 *
	 * @param fileId    The id of the file to remove.
	 * @param projectId the project to delete the reference file for.
	 * @param locale    the locale specified by the browser.
	 * @return Success or error based on the result of deleting the file.
	 * @throws EntityNotFoundException if project or reference file cannot be read
	 */
	public String deleteReferenceFile(Long fileId, Long projectId, Locale locale) throws EntityNotFoundException {
		Project project = projectService.read(projectId);
		ReferenceFile file = referenceFileService.read(fileId);

		try {
			logger.info("Removing file with id of : " + fileId);
			projectService.removeReferenceFileFromProject(project, file);
			return messageSource.getMessage("server.projects.reference-file.delete-success",
							new Object[] { file.getLabel(), project.getName() }, locale);
		} catch (EntityNotFoundException e) {
			logger.error("Failed to remove reference file, reason unknown.", e);
			throw new EntityNotFoundException(messageSource.getMessage("server.projects.reference-file.delete-error",
					new Object[] { file.getLabel(), project.getName() }, locale));
		}
	}

	/**
	 * Download a reference file based on the id passed.
	 *
	 * @param fileId   The id of the file to download
	 * @param response {@link HttpServletResponse} to write to file to
	 * @throws IOException if we fail to read the file from disk.
	 */
	public void downloadReferenceFile(Long fileId, HttpServletResponse response) throws IOException {
		ReferenceFile file = referenceFileService.read(fileId);
		Path path = file.getFile();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}
}

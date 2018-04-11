package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Controller for all {@link ReferenceFile} related views
 *
 */
@Controller
@RequestMapping("/referenceFiles")
public class ReferenceFileController {
	private static final Logger logger = LoggerFactory.getLogger(ReferenceFileController.class);
	// Converters
	Formatter<Date> dateFormatter;
	private final ProjectService projectService;
	private final ReferenceFileService referenceFileService;
	private final MessageSource messageSource;

	@Autowired
	public ReferenceFileController(ProjectService projectService, ReferenceFileService referenceFileService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.referenceFileService = referenceFileService;
		this.messageSource = messageSource;
		this.dateFormatter = new DateFormatter();
	}

	/**
	 * Download a reference file based on the id passed.
	 *
	 * @param fileId
	 *            The id of the file to download
	 * @param response
	 *            {@link HttpServletResponse} to write to file to
	 *
	 * @throws IOException
	 *             if we fail to read the file from disk.
	 */
	@RequestMapping(value = "/download/{fileId}")
	public void downloadReferenceFile(@PathVariable Long fileId,
			HttpServletResponse response) throws IOException {
		ReferenceFile file = referenceFileService.read(fileId);
		Path path = file.getFile();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}

	/**
	 * Add a new reference file to a project.
	 *
	 * @param projectId The id of the project to add the file to.
	 * @param files     {@link List} of {@link MultipartFile} file being uploaded.
	 * @param response  {@link HttpServletResponse}
	 * @param locale    locale of the logged in user
	 * @return Success message if file was successfully uploaded
	 */
	@RequestMapping("/project/{projectId}/new")
	public @ResponseBody Map<String, String> addReferenceFileToProject(@PathVariable Long projectId,
			@RequestParam(value = "file") List<MultipartFile> files, HttpServletResponse response, final Locale locale) {
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
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			return ImmutableMap.of("error_message",
					messageSource.getMessage("projects.meta.reference-file.invalid-content", new Object[] {}, locale));
		} catch (IOException e) {
			logger.error("Error writing sequence file", e);
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			return ImmutableMap.of("error_message",
					messageSource.getMessage("projects.meta.reference-file.unknown-error", new Object[] {}, locale));
		}
		
		// this isn't ever actually parsed by the page that's calling this
		// method so doesn't need to be i18n.
		return ImmutableMap.of("success", "upload complete.");
	}

	/**
	 * Upload a transient reference file, to be used for a single analysis.
	 *
	 * @param file     the new reference file
	 * @param response the response to write errors to.
	 * @param locale   Locale of the current user
	 * @return Success message with uploaded file id and name
	 * @throws IOException if the new reference file cannot be saved
	 */
	@RequestMapping("/new")
	public Map<String, Object> addIndependentReferenceFile(
			final @RequestParam(value = "file") MultipartFile file, final HttpServletResponse response, final Locale locale) throws IOException {
		logger.debug("Adding transient reference file for a single pipeline.");
		// Prepare a new reference file using the multipart file supplied by the caller
		final Path temp = Files.createTempDirectory(null);

		final Path target = temp.resolve(file.getOriginalFilename());
		file.transferTo(target.toFile());

		ReferenceFile referenceFile = new ReferenceFile(target);
		
		try {
			referenceFile = referenceFileService.create(referenceFile);
		} catch (final UnsupportedReferenceFileContentError e) {
			logger.error("User uploaded a reference file that biojava couldn't parse as DNA.", e);
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			return ImmutableMap.of("error",
					messageSource.getMessage("projects.meta.reference-file.invalid-content", new Object[] {}, locale));
		}
		
		// Clean up temporary files
		Files.deleteIfExists(target);
		Files.deleteIfExists(temp);
		
		return ImmutableMap.of("uploaded-file-id", referenceFile.getId(), "uploaded-file-name", referenceFile.getLabel());
	}

	/**
	 * Delete a reference file. This will remove it from the project.
	 *
	 * @param fileId
	 *            The id of the file to remove.
	 * @param projectId
	 *            the project to delete the reference file for.
	 * @param response
	 *            {@link HttpServletResponse} required for returning an error
	 *            state.
	 * @param locale
	 *            the locale specified by the browser.
	 *
	 * @return Success or error based on the result of deleting the file.
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> deleteReferenceFile(@RequestParam Long fileId,
			@RequestParam Long projectId, HttpServletResponse response, Locale locale) {
		Map<String, Object> result = new HashMap<>();
		Project project = projectService.read(projectId);
		ReferenceFile file = referenceFileService.read(fileId);
		try {

			logger.info("Removing file with id of : " + fileId);
			projectService.removeReferenceFileFromProject(project, file);
			result.put("result", "success");
			result.put("msg", messageSource.getMessage("projects.meta.reference-file.delete-success",
					new Object[] { file.getLabel(), project.getName() }, locale));
		} catch (EntityNotFoundException e) {
			// This is required else the client does not know that an error was thrown!
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			logger.error("Failed to upload reference file, reason unknown.", e);
			result.put("result", "error");
			result.put("msg", messageSource.getMessage("projects.meta.reference-file.delete-error",
					new Object[] { file.getLabel(), project.getName() }, locale));
		}
		return result;
	}
}


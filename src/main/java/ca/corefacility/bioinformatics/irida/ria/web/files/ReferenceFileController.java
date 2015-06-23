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

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

import com.google.common.collect.ImmutableMap;

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
	 * @param projectId
	 *            The id of the project to add the file to.
	 * @param file
	 *            {@link MultipartFile} file being uploaded.
	 * @param locale
	 *            the locale specified by the browser.
	 * @param response
	 *            {@link HttpServletResponse}
	 *
	 */
	@RequestMapping("/project/{projectId}/new")
	public void createNewReferenceFile(@PathVariable Long projectId,
			@RequestParam(value = "file") List<MultipartFile> files, HttpServletResponse response) {
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
		} catch (IOException e) {
			logger.error("Error writing sequence file", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
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


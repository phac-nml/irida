package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Controller for all {@link ReferenceFile} related views
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
	 * Upload a transient reference file, to be used for a single analysis.
	 *
	 * @param file     the new reference file
	 * @param response the response to write errors to.
	 * @param locale   Locale of the current user
	 * @return Success message with uploaded file id and name
	 * @throws IOException if the new reference file cannot be saved
	 */
	@RequestMapping("/new")
	public Map<String, Object> addIndependentReferenceFile(final @RequestParam(value = "file") MultipartFile file,
			final HttpServletResponse response, final Locale locale) throws IOException {
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
					messageSource.getMessage("server.projects.reference-file.invalid-content", new Object[] {},
							locale));
		}

		// Clean up temporary files
		Files.deleteIfExists(target);
		Files.deleteIfExists(temp);

		return ImmutableMap.of("uploaded-file-id", referenceFile.getId(), "uploaded-file-name",
				referenceFile.getLabel());
	}

}


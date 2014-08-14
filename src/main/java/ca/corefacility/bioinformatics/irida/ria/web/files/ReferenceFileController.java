package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Controller for all {@link ReferenceFile} related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/referenceFiles")
public class ReferenceFileController {
	private static final Logger logger = LoggerFactory.getLogger(ReferenceFileController.class);
	private ProjectService projectService;
	private ReferenceFileService referenceFileService;

	@Autowired
	public ReferenceFileController(ProjectService projectService, ReferenceFileService referenceFileService) {
		this.projectService = projectService;
		this.referenceFileService = referenceFileService;
	}

	@RequestMapping(value = "/download/{fileId}")
	public void downloadReferenceFile(@PathVariable Long fileId,
			HttpServletResponse response) throws IOException {
		ReferenceFile file = referenceFileService.read(fileId);
		Path path = file.getFile();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}

	@RequestMapping("/project/{projectId}/new")
	public @ResponseBody Map<String, Long> createNewReferenceFile(@PathVariable Long projectId,
			@RequestParam MultipartFile files) throws IOException {

		logger.debug("Adding reference file to project " + projectId);
		logger.trace("Uploaded file size: " + files.getSize() + " bytes");

		Project project = projectService.read(projectId);
		logger.trace("Read project " + projectId);

		// Prepare a new reference file using the mulipart file supplied by the caller
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(files.getOriginalFilename());

		files.transferTo(target.toFile());
		logger.debug("Wrote temp file to " + target);

		ReferenceFile referenceFile = new ReferenceFile(target);
		Join<Project, ReferenceFile> projectReferenceFileJoin = projectService
				.addReferenceFileToProject(project, referenceFile);
		logger.debug("Created reference file in project " + projectId);

		// Clean up temporary files
		Files.deleteIfExists(target);
		Files.deleteIfExists(temp);

		Map<String, Long> result = new HashMap<>();
		result.put("id", projectReferenceFileJoin.getObject().getId());
		return result;
	}
}


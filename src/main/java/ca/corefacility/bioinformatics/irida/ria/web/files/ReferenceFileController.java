package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * Controller for all {@link ReferenceFile} related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/referenceFiles")
public class ReferenceFileController {
	private ProjectService projectService;

	@Autowired
	public ReferenceFileController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@RequestMapping(value = "/download/project/{projectId}/{fileId}")
	public void downloadReferenceFile(@PathVariable Long projectId, @PathVariable Long fileId,
			HttpServletResponse response) throws IOException {
		Project p = projectService.read(projectId);
		ReferenceFile file = null;
		List<Join<Project, ReferenceFile>> filesJoin = projectService.getReferenceFilesForProject(p);
		for (Join<Project, ReferenceFile> join : filesJoin) {
			if (join.getObject().getId() == fileId) {
				file = join.getObject();
				break;
			}
		}
		if (file != null) {
			Path path = file.getFile();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getLabel() + "\"");
			Files.copy(path, response.getOutputStream());
			response.flushBuffer();
		} else {
			throw new EntityNotFoundException(
					"Cannot find reference file with id [" + fileId + "] for project [" + p.getLabel() + "]");
		}
	}

	@RequestMapping("/porject/{projectId}/new")
	public @ResponseBody Map<String, String> createNewReferenceFile(@PathVariable Long projectId) {

	}
}


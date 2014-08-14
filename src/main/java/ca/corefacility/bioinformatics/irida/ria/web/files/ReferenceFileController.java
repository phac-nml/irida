package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Controller for all {@link ReferenceFile} related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/referenceFiles")
public class ReferenceFileController {
	private ReferenceFileService referenceFileService;

	@Autowired
	public ReferenceFileController(ReferenceFileService referenceFileService) {
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

	@RequestMapping("/porject/{projectId}/new")
	public @ResponseBody Map<String, String> createNewReferenceFile(@PathVariable Long projectId) {

	}
}


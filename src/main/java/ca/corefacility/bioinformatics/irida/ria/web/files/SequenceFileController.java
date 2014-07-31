package ca.corefacility.bioinformatics.irida.ria.web.files;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Controller for all {@link SequenceFile} related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/sequenceFiles")
public class SequenceFileController {
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileController.class);
	/*
	 * PAGES
	 */
	public static final String BASE_URL = "sequenceFiles/";
	public static final String FILE_DETAIL_PAGE = BASE_URL + "file_details";

	/*
	 * SERVICES
	 */
	private SequenceFileService sequenceFileService;

	@Autowired
	public SequenceFileController(SequenceFileService sequenceFileService) {
		this.sequenceFileService = sequenceFileService;
	}

	@RequestMapping("/{sequenceFileId}")
	public String getSequenceFilePage(final Model model, @PathVariable Long sequenceFileId) {
		logger.debug("Loading sequence files page for id: " + sequenceFileId);
		SequenceFile file = sequenceFileService.read(sequenceFileId);
		model.addAttribute("file", file);
		return FILE_DETAIL_PAGE;
	}

	@RequestMapping("/download/{sequenceFileId}")
	public void downloadSequenceFile(@PathVariable Long sequenceFileId, HttpServletResponse response) throws IOException {
		SequenceFile sequenceFile = sequenceFileService.read(sequenceFileId);
		Path path = sequenceFile.getFile();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + sequenceFile.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * Controller for all {@link SequenceFile} related views
 *
 */
@Controller
public class SequenceFileController {
	/*
	 * PAGES
	 */
	public static final String BASE_URL = "sequenceFiles/";
	public static final String FILE_DETAIL_PAGE = BASE_URL + "file_details";
	public static final String FILE_OVERREPRESENTED = BASE_URL + "file_overrepresented";
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileController.class);
	/*
	 * SUB NAV
	 */
	public static final String ACTIVE_NAV = "activeNav";
	private static final String ACTIVE_NAV_DASHBOARD = "dashboard";
	private static final String ACTIVE_NAV_OVERREPRESENTED = "overrepresented";
	public static final String IMG_PERBASE = "perbase";
	public static final String IMG_PERSEQUENCE = "persequence";
	public static final String IMG_DUPLICATION_LEVEL = "duplicationlevel";
	/*
	 * CONVERSIONS
	 */
	Formatter<Date> dateFormatter;
	/*
	 * SERVICES
	 */
	private SequenceFileService sequenceFileService;
	private SequencingRunService sequencingRunService;

	@Autowired
	public SequenceFileController(SequenceFileService sequenceFileService, SequencingRunService sequencingRunService) {
		this.sequenceFileService = sequenceFileService;
		this.sequencingRunService = sequencingRunService;
		this.dateFormatter = new DateFormatter();
	}

	/**
	 * Gets the name of the template for the sequence file chart / main page.
	 * Populates the template with the standard info.
	 *
	 * @param model
	 *            {@link Model}
	 * @param sequenceFileId
	 *            Id for the sequence file
	 * @return The name of the template.
	 */
	@RequestMapping(value = { "/sequenceFiles/{sequenceFileId}/summary",
			"/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequenceFileId}/summary",
			"/sequencingRuns/{runId}/sequenceFiles/{sequenceFileId}/summary" })
	public String getSequenceFilePage(final Model model, @PathVariable Long sequenceFileId) {
		logger.debug("Loading sequence files page for id: " + sequenceFileId);
		createDefaultPageInfo(sequenceFileId, model);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_DASHBOARD);
		return FILE_DETAIL_PAGE;
	}

	/**
	 * Gets the name of the template for the sequence file overrepresented
	 * sequences page. Populates the template with the standard info.
	 *
	 * @param model
	 *            {@link Model}
	 * @param sequenceFileId
	 *            Id for the sequence file.
	 * @return The name fo the template
	 */
	@RequestMapping(value = { "/sequenceFiles/{sequenceFileId}/overrepresented",
			"/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequenceFileId}/overrepresented",
			"/sequencingRuns/{runId}/sequenceFiles/{sequenceFileId}/overrepresented" })
	public String getSequenceFileOverrepresentedPage(final Model model, @PathVariable Long sequenceFileId) {
		logger.debug("Loading sequence files page for id: " + sequenceFileId);
		createDefaultPageInfo(sequenceFileId, model);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_OVERREPRESENTED);
		return FILE_OVERREPRESENTED;
	}

	/**
	 * Downloads a sequence file.
	 *
	 * @param sequenceFileId
	 *            Id for the file to download.
	 * @param response
	 *            {@link HttpServletResponse}
	 * @throws IOException
	 *             if we can't write the file to the response.
	 */
	@RequestMapping("/sequenceFiles/download/{sequenceFileId}")
	public void downloadSequenceFile(@PathVariable Long sequenceFileId, HttpServletResponse response)
			throws IOException {
		SequenceFile sequenceFile = sequenceFileService.read(sequenceFileId);
		Path path = sequenceFile.getFile();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + sequenceFile.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}

	/**
	 * Get images specific for individual sequence files.
	 *
	 * @param sequenceFileId
	 *            Id for the sequnece file.
	 * @param type
	 *            The type of image to get.
	 * @param response
	 *            {@link HttpServletResponse}
	 * @throws IOException
	 *             if we can't write the image out to the response.
	 */
	@RequestMapping(value = "/sequenceFiles/img/{sequenceFileId}/{type}", produces = MediaType.IMAGE_PNG_VALUE)
	public void downloadSequenceFileImages(@PathVariable Long sequenceFileId, @PathVariable String type,
			HttpServletResponse response) throws IOException {
		SequenceFile file = sequenceFileService.read(sequenceFileId);
		AnalysisFastQC fastQC = sequenceFileService.getFastQCAnalysisForSequenceFile(file);
		if (fastQC != null) {
			byte[] chart = new byte[0];
			if (type.equals(IMG_PERBASE)) {
				chart = fastQC.getPerBaseQualityScoreChart();
			} else if (type.equals(IMG_PERSEQUENCE)) {
				chart = fastQC.getPerSequenceQualityScoreChart();
			} else if (type.equals(IMG_DUPLICATION_LEVEL)) {
				chart = fastQC.getDuplicationLevelChart();
			} else {
				throw new EntityNotFoundException("Image not found");
			}
			response.getOutputStream().write(chart);
		}
		response.flushBuffer();
	}

	/**
	 * Populates the model with the default information for a file.
	 *
	 * @param sequenceFileId
	 *            Id for the sequence file.
	 * @param model
	 *            {@link Model}
	 */
	private void createDefaultPageInfo(Long sequenceFileId, Model model) {
		SequenceFile file = sequenceFileService.read(sequenceFileId);
		SequencingRun run = sequencingRunService.getSequencingRunForSequenceFile(file);
		AnalysisFastQC fastQC = sequenceFileService.getFastQCAnalysisForSequenceFile(file);
		model.addAttribute("file", file);
		model.addAttribute("created", dateFormatter.print(file.getCreatedDate(), LocaleContextHolder.getLocale()));
		model.addAttribute("fastQC", fastQC);
		model.addAttribute("run", run);
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.imgscalr.Scalr;
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
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
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

	public static final String FS = BASE_URL + "fastqc";

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
	private SequencingObjectService sequencingObjectService;
	private final AnalysisService analysisService;

	@Autowired
	public SequenceFileController(SequencingObjectService sequencingObjectService, SequencingRunService sequencingRunService,
			final AnalysisService analysisService) {
		this.sequencingObjectService = sequencingObjectService;
		this.dateFormatter = new DateFormatter();
		this.analysisService = analysisService;
	}

	/**
	 * Gets the name of the template for the sequence file chart / main page.
	 * Populates the template with the standard info.
	 *
	 * @param model              {@link Model}
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @return The name of the template.
	 */
	@RequestMapping(value = {
			"/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/summary/**",
			"/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/**",
			"/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/summary/**",
			"/sequencingRuns/{runId}/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/summary/**" })
	public String getSequenceFilePage(final Model model, @PathVariable Long sequencingObjectId,
			@PathVariable Long sequenceFileId, @PathVariable(required = false) Long sampleId, @PathVariable(required = false) Long projectId,
			@PathVariable(required = false) Long runId) {
		logger.debug("Loading sequence files page for id: " + sequenceFileId);
		model.addAttribute("seqObjectId", sequencingObjectId);
		model.addAttribute("seqFileId", sequenceFileId);
		model.addAttribute("sampleId", sampleId);
		model.addAttribute("projectId", projectId);
		model.addAttribute("runId", runId);
		return FS;
	}

	/**
	 * Gets the name of the template for the sequence file overrepresented
	 * sequences page. Populates the template with the standard info.
	 *
	 * @param model              {@link Model}
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @return The name fo the template
	 */
	@RequestMapping(value = {
			"/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/overrepresented",
			"/projects/{projectId}/samples/{sampleId}/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/overrepresented",
			"/sequencingRuns/{runId}/sequenceFiles/{sequencingObjectId}/file/{sequenceFileId}/overrepresented" })
	public String getSequenceFileOverrepresentedPage(final Model model, @PathVariable Long sequencingObjectId,
			@PathVariable Long sequenceFileId) {
		logger.debug("Loading sequence files page for id: " + sequenceFileId);
		createDefaultPageInfo(sequencingObjectId, sequenceFileId, model);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_OVERREPRESENTED);
		return FILE_OVERREPRESENTED;
	}

	/**
	 * Downloads a sequence file.
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @param response           {@link HttpServletResponse}
	 * @throws IOException if we can't write the file to the response.
	 */
	@RequestMapping("/sequenceFiles/download/{sequencingObjectId}/file/{sequenceFileId}")
	public void downloadSequenceFile(@PathVariable Long sequencingObjectId, @PathVariable Long sequenceFileId,
			HttpServletResponse response) throws IOException {
		SequencingObject sequencingObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile sequenceFile = sequencingObject.getFileWithId(sequenceFileId);
		Path path = sequenceFile.getFile();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + sequenceFile.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}

	/**
	 * Get images specific for individual sequence files.
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @param type               The type of image to get.
	 * @param response           {@link HttpServletResponse}
	 * @param thumb              Whether to scale the image for a thumbnail
	 * @throws IOException if we can't write the image out to the response.
	 */
	@RequestMapping(value = "/sequenceFiles/img/{sequencingObjectId}/file/{sequenceFileId}/{type}")
	public void downloadSequenceFileImages(@PathVariable Long sequencingObjectId, @PathVariable Long sequenceFileId,
			@PathVariable String type, HttpServletResponse response, @RequestParam(defaultValue = "false") boolean thumb)
			throws IOException {
		SequencingObject sequencingObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile file = sequencingObject.getFileWithId(sequenceFileId);
		AnalysisFastQC fastQC = analysisService.getFastQCAnalysisForSequenceFile(sequencingObject, file.getId());
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
			if (thumb) {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(chart));
				BufferedImage thumbnail = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 160,
						Scalr.OP_ANTIALIAS);
				ImageIO.write(thumbnail, "png", response.getOutputStream());
			} else {
				response.getOutputStream().write(chart);
			}
		}
		response.setContentType(MediaType.IMAGE_PNG_VALUE);
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
	private void createDefaultPageInfo(Long sequencingObjectId, Long sequenceFileId, Model model) {
		SequencingObject seqObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile file = seqObject.getFileWithId(sequenceFileId);
		AnalysisFastQC fastQC = analysisService.getFastQCAnalysisForSequenceFile(seqObject, file.getId());
		model.addAttribute("sequencingObject", seqObject);
		model.addAttribute("file", file);
		model.addAttribute("created", dateFormatter.print(file.getCreatedDate(), LocaleContextHolder.getLocale()));
		model.addAttribute("fastQC", fastQC);
	}
}

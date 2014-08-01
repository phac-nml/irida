package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

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

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

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
	private AnalysisService analysisService;

	/*
	 * CONVERSIONS
	 */
	Formatter<Date> dateFormatter;

	@Autowired
	public SequenceFileController(SequenceFileService sequenceFileService, AnalysisService analysisService) {
		this.sequenceFileService = sequenceFileService;
		this.analysisService = analysisService;
		this.dateFormatter = new DateFormatter();
	}

	@RequestMapping("/{sequenceFileId}")
	public String getSequenceFilePage(final Model model, @PathVariable Long sequenceFileId) {
		logger.debug("Loading sequence files page for id: " + sequenceFileId);
		SequenceFile file = sequenceFileService.read(sequenceFileId);
		AnalysisFastQC fastQC = getFastQCAnalysis(file);
		model.addAttribute("file", file);
		model.addAttribute("created", dateFormatter.print(file.getTimestamp(), LocaleContextHolder.getLocale()));
		model.addAttribute("fastQC", fastQC);
		model.addAttribute("perbase", "/sequenceFiles/img/" + sequenceFileId + "-perbase.png");
		model.addAttribute("persequence", "/sequenceFiles/img/" + sequenceFileId + "-persequence.png");
		model.addAttribute("dublicationlevel", "/sequenceFiles/img/" + sequenceFileId + "-dublicationlevel.png");
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

	@RequestMapping(value = "/img/{sequenceFileId}-{type}", produces = MediaType.IMAGE_PNG_VALUE)
	public void downloadSequenceFileImages(@PathVariable Long sequenceFileId, @PathVariable String type, HttpServletResponse response) throws IOException {
		SequenceFile file = sequenceFileService.read(sequenceFileId);
		AnalysisFastQC fastQC = getFastQCAnalysis(file);
		byte[] chart;
		if(type.equals("perbase")) {
			chart = fastQC.getPerBaseQualityScoreChart();
		}
		else if(type.equals("persequence")) {
			chart = fastQC.getPerSequenceQualityScoreChart();
		}
		else {
			chart = fastQC.getDuplicationLevelChart();
		}
		response.getOutputStream().write(chart);
		response.flushBuffer();
	}

	private AnalysisFastQC getFastQCAnalysis(SequenceFile file) {
		Set<AnalysisFastQC> analysis = analysisService.getAnalysesForSequenceFile(file, AnalysisFastQC.class);
		return analysis.iterator().next();
	}
}

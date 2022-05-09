package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCImagesResponse;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

/**
 * UI Service for handling requests related to {@link SequenceFile}s
 */

@Component
public class UISequenceFileService {
	private AnalysisService analysisService;
	private SequencingObjectService sequencingObjectService;

	public static final String IMG_PERBASE = "perbase";
	public static final String IMG_PERSEQUENCE = "persequence";
	public static final String IMG_DUPLICATION_LEVEL = "duplicationlevel";
	public static final String[] qcFileTypes = { "perbase", "persequence", "duplicationlevel" };

	@Autowired
	public UISequenceFileService(AnalysisService analysisService, SequencingObjectService sequencingObjectService) {
		this.analysisService = analysisService;
		this.sequencingObjectService = sequencingObjectService;
	}

	/**
	 * Gets the details for the sequence file
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @return {@link FastQCDetailsResponse} dto which contains the sequencing object, sequence file, and the fastqc
	 * result.
	 */
	public FastQCDetailsResponse getFastQCDetails(Long sequencingObjectId, Long sequenceFileId) {
		SequencingObject seqObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile file = seqObject.getFileWithId(sequenceFileId);
		AnalysisFastQC fastQC = analysisService.getFastQCAnalysisForSequenceFile(seqObject, file.getId());

		return new FastQCDetailsResponse(seqObject, file, fastQC);
	}

	/**
	 * Gets the fastqc charts for the file.
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @return {@link FastQCImagesResponse} dto which has the byte arrays for the images as well as the fastqc version
	 * @throws IOException if entity is not found
	 */
	public FastQCImagesResponse getFastQCCharts(Long sequencingObjectId, Long sequenceFileId) throws IOException {
		SequencingObject sequencingObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile file = sequencingObject.getFileWithId(sequenceFileId);
		AnalysisFastQC fastQC = analysisService.getFastQCAnalysisForSequenceFile(sequencingObject, file.getId());

		byte[] perBaseChart = new byte[0];
		byte[] perSequenceChart = new byte[0];
		byte[] duplicationLevelChart = new byte[0];

		for (String type : qcFileTypes) {
			if (type.equals(IMG_PERBASE)) {
				perBaseChart = fastQC.getPerBaseQualityScoreChart();
			} else if (type.equals(IMG_PERSEQUENCE)) {
				perSequenceChart = fastQC.getPerSequenceQualityScoreChart();
			} else if (type.equals(IMG_DUPLICATION_LEVEL)) {
				duplicationLevelChart = fastQC.getDuplicationLevelChart();
			} else {
				throw new IOException("Image not found");
			}
		}
		return new FastQCImagesResponse(perBaseChart, perSequenceChart, duplicationLevelChart,
				fastQC.getFastqcVersion());
	}

	/**
	 * Gets the overrepresented sequences for the file
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @return {@link AnalysisFastQC} model
	 */
	public AnalysisFastQC getOverRepresentedSequences(Long sequencingObjectId, Long sequenceFileId) {
		SequencingObject sequencingObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile file = sequencingObject.getFileWithId(sequenceFileId);
		AnalysisFastQC fastQC = analysisService.getFastQCAnalysisForSequenceFile(sequencingObject, file.getId());

		return fastQC;
	}

	/**
	 * Download the sequence file
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 */
	public void downloadSequenceFile(Long sequencingObjectId, Long sequenceFileId, HttpServletResponse response)
			throws IOException {
		SequencingObject sequencingObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile sequenceFile = sequencingObject.getFileWithId(sequenceFileId);
		Path path = sequenceFile.getFile();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + sequenceFile.getLabel() + "\"");
		Files.copy(path, response.getOutputStream());
		response.flushBuffer();
	}
}

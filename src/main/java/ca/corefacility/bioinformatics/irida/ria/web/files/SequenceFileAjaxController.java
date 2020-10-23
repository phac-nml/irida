package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCImagesResponse;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

/**
 * Ajax controller to get data for the fastqc page.
 */
@RestController
@Scope("session")
@RequestMapping("/ajax/sequenceFiles")
public class SequenceFileAjaxController {

	private AnalysisService analysisService;
	private SequencingObjectService sequencingObjectService;

	public static final String IMG_PERBASE = "perbase";
	public static final String IMG_PERSEQUENCE = "persequence";
	public static final String IMG_DUPLICATION_LEVEL = "duplicationlevel";

	public static final String[] qcFileTypes = { "perbase", "persequence", "duplicationlevel" };

	@Autowired
	public SequenceFileAjaxController(AnalysisService analysisService,
			SequencingObjectService sequencingObjectService) {
		this.analysisService = analysisService;
		this.sequencingObjectService = sequencingObjectService;
	}

	/**
	 * Gets the details for the file fastqc results.
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @return {@link FastQCDetailsResponse} dto which contains the sequencing object,
	 * sequence file, and the fastqc result.
	 */
	@GetMapping("/fastqc-details")
	public FastQCDetailsResponse getFastQCDetails(@RequestParam Long sequencingObjectId,
			@RequestParam Long sequenceFileId) {
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
	 * @return {@link FastQCImagesResponse} dto which has the byte arrays for the images
	 * as well as the fastqc version
	 */
	@GetMapping("/fastqc-charts")
	public ResponseEntity<FastQCImagesResponse> getFastQCCharts(@RequestParam Long sequencingObjectId,
			@RequestParam Long sequenceFileId) throws IOException {
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
				throw new EntityNotFoundException("Image not found");
			}
		}

		return ResponseEntity.ok(new FastQCImagesResponse(perBaseChart, perSequenceChart, duplicationLevelChart,
				fastQC.getFastqcVersion()));
	}

	/**
	 * Gets the overrepresented sequences for the file from the
	 * fastqc results.
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @return {@link AnalysisFastQC} model
	 */
	@GetMapping("/overrepresented-sequences")
	public ResponseEntity<AnalysisFastQC> getOverRepresentedSequences(@RequestParam Long sequencingObjectId,
			@RequestParam Long sequenceFileId) {
		SequencingObject sequencingObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile file = sequencingObject.getFileWithId(sequenceFileId);
		AnalysisFastQC fastQC = analysisService.getFastQCAnalysisForSequenceFile(sequencingObject, file.getId());

		return ResponseEntity.ok(fastQC);
	}
}

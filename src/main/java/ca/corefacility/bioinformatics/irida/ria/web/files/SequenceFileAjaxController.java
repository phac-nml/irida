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
import ca.corefacility.bioinformatics.irida.ria.web.services.UISequenceFileService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

/**
 * Ajax controller to get data for the fastqc page.
 */
@RestController
@Scope("session")
@RequestMapping("/ajax/sequenceFiles")
public class SequenceFileAjaxController {
	private UISequenceFileService uiSequenceFileService;

	@Autowired
	public SequenceFileAjaxController(UISequenceFileService uiSequenceFileService) {
		this.uiSequenceFileService = uiSequenceFileService;
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
	public ResponseEntity<FastQCDetailsResponse> getFastQCDetails(@RequestParam Long sequencingObjectId,
			@RequestParam Long sequenceFileId) {
		return ResponseEntity.ok(uiSequenceFileService.getFastQCDetails(sequencingObjectId, sequenceFileId));
	}

	/**
	 * Gets the fastqc charts for the file.
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @return {@link FastQCImagesResponse} dto which has the byte arrays for the images
	 * as well as the fastqc version
	 * @throws IOException if entity is not found
	 */
	@GetMapping("/fastqc-charts")
	public ResponseEntity<FastQCImagesResponse> getFastQCCharts(@RequestParam Long sequencingObjectId,
			@RequestParam Long sequenceFileId) throws IOException {

		return ResponseEntity.ok(uiSequenceFileService.getFastQCCharts(sequencingObjectId, sequenceFileId));
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
		return ResponseEntity.ok(uiSequenceFileService.getOverRepresentedSequences(sequencingObjectId, sequenceFileId));
	}
}

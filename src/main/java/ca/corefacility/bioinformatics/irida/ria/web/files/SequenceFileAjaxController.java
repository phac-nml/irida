package ca.corefacility.bioinformatics.irida.ria.web.files;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCImagesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISequenceFileService;

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
	 * @return {@link FastQCDetailsResponse} dto which contains the sequencing object, sequence file, and the fastqc
	 * result.
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
	 * @return {@link FastQCImagesResponse} dto which has the byte arrays for the images as well as the fastqc version
	 * @throws IOException if entity is not found
	 */
	@GetMapping("/fastqc-charts")
	public ResponseEntity<FastQCImagesResponse> getFastQCCharts(@RequestParam Long sequencingObjectId,
			@RequestParam Long sequenceFileId) throws IOException {
		return ResponseEntity.ok(uiSequenceFileService.getFastQCCharts(sequencingObjectId, sequenceFileId));
	}

	/**
	 * Download the sequence file
	 *
	 * @param sequencingObjectId ID for the {@link SequencingObject}
	 * @param sequenceFileId     Id for the {@link SequenceFile}
	 * @param response           HTTP response object
	 * @throws IOException if file is not found
	 */
	@GetMapping("/download")
	public void downloadSequenceFile(@RequestParam Long sequencingObjectId, @RequestParam Long sequenceFileId,
			HttpServletResponse response) throws IOException {
		uiSequenceFileService.downloadSequenceFile(sequencingObjectId, sequenceFileId, response);
	}
}

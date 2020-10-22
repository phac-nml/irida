package ca.corefacility.bioinformatics.irida.ria.web.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCDetailsResponse;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

@RestController
@Scope("session")
@RequestMapping("/ajax/sequenceFiles")
public class SequenceFileAjaxController {

	private AnalysisService analysisService;
	private SequencingObjectService sequencingObjectService;

	@Autowired
	public SequenceFileAjaxController(AnalysisService analysisService, SequencingObjectService sequencingObjectService) {
		this.analysisService = analysisService;
		this.sequencingObjectService = sequencingObjectService;
	}

	@GetMapping("/fastqc-details")
	public FastQCDetailsResponse getFastQCDetails(@RequestParam Long sequencingObjectId, @RequestParam Long sequenceFileId) {

		SequencingObject seqObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile file = seqObject.getFileWithId(sequenceFileId);
		AnalysisFastQC fastQC = analysisService.getFastQCAnalysisForSequenceFile(seqObject, file.getId());

		return new FastQCDetailsResponse(seqObject, file, fastQC);
	}

	@GetMapping("/fastqc-images")
	public void getFastQCImages(@RequestParam Long sequencingObjectId, @RequestParam Long sequenceFileId) {

	}

	@GetMapping("/overrepresented-sequences")
	public void getOverRepresentedSequences(@RequestParam Long sequencingObjectId, @RequestParam Long sequenceFileId) {

	}
}

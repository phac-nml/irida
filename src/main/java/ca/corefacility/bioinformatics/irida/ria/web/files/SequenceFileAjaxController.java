package ca.corefacility.bioinformatics.irida.ria.web.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCDetailsResponse;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

@RestController
@Scope("session")
@RequestMapping("/ajax/sequenceFiles")
public class SequenceFileAjaxController {

	private SequencingObjectService sequencingObjectService;

	@Autowired
	public SequenceFileAjaxController(SequencingObjectService sequencingObjectService) {
		this.sequencingObjectService = sequencingObjectService;
	}

	@GetMapping("/fastqc-details")
	public FastQCDetailsResponse getFastQCDetails(@RequestParam Long sequencingObjectId, @RequestParam Long sequenceFileId) {

		SequencingObject seqObject = sequencingObjectService.read(sequencingObjectId);
		SequenceFile file = seqObject.getFileWithId(sequenceFileId);

		return new FastQCDetailsResponse(seqObject, file);
	}

	@GetMapping("/fastqc-images")
	public void getFastQCImages(@RequestParam Long sequencingObjectId, @RequestParam Long sequenceFileId) {

	}

	@GetMapping("/overrepresented-sequences")
	public void getOverRepresentedSequences(@RequestParam Long sequencingObjectId, @RequestParam Long sequenceFileId) {

	}
}

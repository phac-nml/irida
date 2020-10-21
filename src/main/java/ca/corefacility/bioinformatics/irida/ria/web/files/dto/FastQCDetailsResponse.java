package ca.corefacility.bioinformatics.irida.ria.web.files.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

public class FastQCDetailsResponse {
	private SequencingObject sequencingObject;
	private SequenceFile sequenceFile;

	public FastQCDetailsResponse(SequencingObject sequencingObject, SequenceFile sequenceFile) {
		this.sequencingObject = sequencingObject;
		this.sequenceFile = sequenceFile;
	}

	public SequencingObject getSequencingObject() {
		return sequencingObject;
	}

	public void setSequencingObject(SequencingObject sequencingObject) {
		this.sequencingObject = sequencingObject;
	}

	public SequenceFile getSequenceFile() {
		return sequenceFile;
	}

	public void setSequenceFile(SequenceFile sequenceFile) {
		this.sequenceFile = sequenceFile;
	}
}

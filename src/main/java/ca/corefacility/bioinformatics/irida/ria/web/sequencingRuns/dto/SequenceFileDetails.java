package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Used as a response for encapsulating a sequence file and its file size
 */

public class SequenceFileDetails {
	private SequenceFile sequenceFile;
	private String fileSize;

	public SequenceFileDetails(SequenceFile sequenceFile, String fileSize) {
		this.sequenceFile = sequenceFile;
		this.fileSize = fileSize;
	}

	public SequenceFile getSequenceFile() {
		return sequenceFile;
	}

	public void setSequenceFile(SequenceFile sequenceFile) {
		this.sequenceFile = sequenceFile;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
}

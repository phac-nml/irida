package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;

public class SingleEndFiles {
	private SingleEndSequenceFile singleEndSequenceFile;
	private String fileSize;

	public SingleEndFiles(SingleEndSequenceFile singleEndSequenceFile, String fileSize) {
		this.singleEndSequenceFile = singleEndSequenceFile;
		this.fileSize = fileSize;
	}

	public SingleEndSequenceFile getSingleEndSequenceFile() {
		return singleEndSequenceFile;
	}

	public void setSingleEndSequenceFile(SingleEndSequenceFile singleEndSequenceFile) {
		this.singleEndSequenceFile = singleEndSequenceFile;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;

/**
 * Used as a response for encapsulating a fast5object and its file size
 */

public class Fast5Files {
	private Fast5Object fast5Object;
	private String fileSize;

	public Fast5Files(Fast5Object fast5Object, String fileSize) {
		this.fast5Object = fast5Object;
		this.fileSize = fileSize;
	}

	public Fast5Object getFast5Object() {
		return fast5Object;
	}

	public void setFast5Object(Fast5Object fast5Object) {
		this.fast5Object = fast5Object;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
}

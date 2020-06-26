package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

public class PairedEndFiles {
	private SequenceFilePair pair;
	private String forwardFileSize;
	private String reverseFileSize;

	public PairedEndFiles(SequenceFilePair pair, String forwardFileSize, String reverseFileSize) {
		this.pair = pair;
		this.forwardFileSize = forwardFileSize;
		this.reverseFileSize = reverseFileSize;
	}

	public SequenceFilePair getPair() {
		return pair;
	}

	public void setPair(SequenceFilePair pair) {
		this.pair = pair;
	}

	public String getForwardFileSize() {
		return forwardFileSize;
	}

	public void setForwardFileSize(String forwardFileSize) {
		this.forwardFileSize = forwardFileSize;
	}

	public String getReverseFileSize() {
		return reverseFileSize;
	}

	public void setReverseFileSize(String reverseFileSize) {
		this.reverseFileSize = reverseFileSize;
	}
}

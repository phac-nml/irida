package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Represents a {@link SequenceFile} on the UI sequencing run create samples page.
 */
public class SequenceFilePairModel {

	private SequenceFileDetails forward;

	private SequenceFileDetails reverse;

	public SequenceFilePairModel() {
	}

	public SequenceFilePairModel(SequenceFileDetails forward, SequenceFileDetails reverse) {
		this.forward = forward;
		this.reverse = reverse;
	}

	public SequenceFileDetails getForward() {
		return forward;
	}

	public void setForward(SequenceFileDetails forward) {
		this.forward = forward;
	}

	public SequenceFileDetails getReverse() {
		return reverse;
	}

	public void setReverse(SequenceFileDetails reverse) {
		this.reverse = reverse;
	}
}


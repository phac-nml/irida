package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import java.util.List;

/**
 * Used as a response for encapsulating a sequenceObjectId and a sequence
 * file details object (sequence file and its size)
 */

public class SequencingObjectDetails {
	private Long sequencingObjectId;
	private List<SequenceFileDetails> sequenceFileDetailsList;

	public SequencingObjectDetails(Long sequencingObjectId, List<SequenceFileDetails> sequenceFileDetailsList) {
		this.sequencingObjectId = sequencingObjectId;
		this.sequenceFileDetailsList = sequenceFileDetailsList;
	}

	public Long getSequencingObjectId() {
		return sequencingObjectId;
	}

	public void setSequencingObjectId(Long sequencingObjectId) {
		this.sequencingObjectId = sequencingObjectId;
	}

	public List<SequenceFileDetails> getSequenceFileDetailsList() {
		return sequenceFileDetailsList;
	}

	public void setSequenceFileDetailsList(List<SequenceFileDetails> sequenceFileDetailsList) {
		this.sequenceFileDetailsList = sequenceFileDetailsList;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Used as a response for encapsulating analysis paired end sample data
 */


public class AnalysisSamples {
	private String sampleName;
	private Long sampleId;
	private Long sequenceFilePairId;
	private SequenceFile forward;
	private SequenceFile reverse;

	public AnalysisSamples() {
	}

	public AnalysisSamples(String sampleName, Long sampleId, Long sequenceFilePairId, SequenceFile forward, SequenceFile reverse) {
		this.sampleName=sampleName;
		this.sampleId=sampleId;
		this.sequenceFilePairId=sequenceFilePairId;
		this.forward=forward;
		this.reverse=reverse;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public Long getSequenceFilePairId() {
		return sequenceFilePairId;
	}

	public void setSequenceFilePairId(Long sequenceFilePairId) {
		this.sequenceFilePairId = sequenceFilePairId;
	}

	public SequenceFile getForward() {
		return forward;
	}

	public void setForward(SequenceFile forward) {
		this.forward = forward;
	}

	public SequenceFile getReverse() {
		return reverse;
	}

	public void setReverse(SequenceFile reverse) {
		this.reverse = reverse;
	}
}

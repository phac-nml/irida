package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Used as a response for encapsulating analysis single end sample data
 */


public class AnalysisSingleEndSamples {
	private String sampleName;
	private Long sampleId;
	private Long singleEndSequenceFileId;
	private SequenceFile sequenceFile;

	public AnalysisSingleEndSamples() {
	}

	public AnalysisSingleEndSamples(String sampleName, Long sampleId, Long singleEndSequenceFileId, SequenceFile sequenceFile) {
		this.sampleName = sampleName;
		this.sampleId = sampleId;
		this.singleEndSequenceFileId = singleEndSequenceFileId;
		this.sequenceFile = sequenceFile;
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

	public Long getFileId() {
		return singleEndSequenceFileId;
	}

	public void setFileId(Long singleEndSequenceFileId) {
		this.singleEndSequenceFileId = singleEndSequenceFileId;
	}

	public SequenceFile getSequenceFile() {
		return sequenceFile;
	}

	public void setSequenceFile(SequenceFile sequenceFile) {
		this.sequenceFile = sequenceFile;
	}
}

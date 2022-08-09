package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import java.util.List;

/**
 * Represents a sample on the UI sequencing run create samples page.
 */
public class SampleModel {
	private Long projectId;
	private Long sampleId;
	private String sampleName;
	private List<SequenceFilePairModel> pairs;

	public SampleModel() {
	}

	public SampleModel(Long projectId, Long sampleId, String sampleName, List<SequenceFilePairModel> pairs) {
		this.projectId = projectId;
		this.sampleId = sampleId;
		this.sampleName = sampleName;
		this.pairs = pairs;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public List<SequenceFilePairModel> getPairs() {
		return pairs;
	}

	public void setPairs(List<SequenceFilePairModel> pairs) {
		this.pairs = pairs;
	}
}

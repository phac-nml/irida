package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

public class AnalysisSampleProject {
	private Long sampleId;
	private Long projectId;

	public AnalysisSampleProject(Long sampleId, Long projectId) {
		this.sampleId = sampleId;
		this.projectId = projectId;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

public class AnalysisName {
	private Long analysisSubmissionId;
	private String analysisName;

	public AnalysisName() {
	}

	public AnalysisName(Long submissionId, String analysisName) {
		this.analysisSubmissionId = submissionId;
		this.analysisName = analysisName;
	}

	public Long getAnalysisSubmissionId() {
		return analysisSubmissionId;
	}

	public void setAnalysisSubmissionId(Long submissionId) {
		this.analysisSubmissionId = submissionId;
	}

	public String getAnalysisName() {
		return analysisName;
	}

	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}
}
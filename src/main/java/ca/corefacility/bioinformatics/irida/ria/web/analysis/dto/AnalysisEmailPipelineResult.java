package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

public class AnalysisEmailPipelineResult {
	private Long analysisSubmissionId;
	private boolean emailPipelineResult;

	public AnalysisEmailPipelineResult() {
	}

	public AnalysisEmailPipelineResult(Long submissionId, boolean emailPipelineResult) {
		this.analysisSubmissionId = submissionId;
		this.emailPipelineResult = emailPipelineResult;
	}

	public Long getAnalysisSubmissionId() {
		return analysisSubmissionId;
	}

	public void setAnalysisSubmissionId(Long submissionId) {
		this.analysisSubmissionId = submissionId;
	}

	public boolean getEmailPipelineResult() {
		return emailPipelineResult;
	}

	public void setEmailPipelineResult(boolean emailPipelineResult) {
		this.emailPipelineResult = emailPipelineResult;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * Used as a request to update if a user should receive
 * email an email upon completion or error..
 */

public class AnalysisEmailPipelineResult {
	private Long analysisSubmissionId;
	private boolean emailPipelineResultCompleted;
	private boolean emailPipelineResultError;

	public AnalysisEmailPipelineResult() {
	}

	public AnalysisEmailPipelineResult(Long submissionId, boolean emailPipelineResultCompleted, boolean emailPipelineResultError) {
		this.analysisSubmissionId = submissionId;
		this.emailPipelineResultCompleted = emailPipelineResultCompleted;
		this.emailPipelineResultError = emailPipelineResultError;
	}

	public Long getAnalysisSubmissionId() {
		return analysisSubmissionId;
	}

	public void setAnalysisSubmissionId(Long submissionId) {
		this.analysisSubmissionId = submissionId;
	}

	public boolean getEmailPipelineResultCompleted() {
		return emailPipelineResultCompleted;
	}

	public void setEmailPipelineResultCompleted(boolean emailPipelineResultCompleted) {
		this.emailPipelineResultCompleted = emailPipelineResultCompleted;
	}

	public boolean getEmailPipelineResultError() {
		return emailPipelineResultError;
	}

	public void setEmailPipelineResultError(boolean emailPipelineResultError) {
		this.emailPipelineResultError = emailPipelineResultError;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * Used as a request to update if a user should receive
 * email an email upon completion or error..
 */

public class AnalysisEmailPipelineResult {
	private Long analysisSubmissionId;
	private Boolean emailPipelineResultCompleted;
	private Boolean emailPipelineResultError;

	public AnalysisEmailPipelineResult() {
	}

	public AnalysisEmailPipelineResult(Long submissionId, Boolean emailPipelineResultCompleted, Boolean emailPipelineResultError) {
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

	public Boolean getEmailPipelineResultCompleted() {
		return emailPipelineResultCompleted;
	}

	public void setEmailPipelineResultCompleted(Boolean emailPipelineResultCompleted) {
		this.emailPipelineResultCompleted = emailPipelineResultCompleted;
	}

	public Boolean getEmailPipelineResultError() {
		return emailPipelineResultError;
	}

	public void setEmailPipelineResultError(Boolean emailPipelineResultError) {
		this.emailPipelineResultError = emailPipelineResultError;
	}
}

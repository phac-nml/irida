package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Used as a request to update the analysis name and/or priority.
 */

public class AnalysisSubmissionInfo {
	private Long analysisSubmissionId;
	private String analysisName;
	private AnalysisSubmission.Priority priority;

	public AnalysisSubmissionInfo() {
	}

	public AnalysisSubmissionInfo(Long submissionId, String analysisName, AnalysisSubmission.Priority priority) {
		this.analysisSubmissionId = submissionId;
		this.analysisName = analysisName;
		this.priority = priority;
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

	public AnalysisSubmission.Priority getPriority() {
		return priority;
	}

	public void setPriority(AnalysisSubmission.Priority priority) {
		this.priority = priority;
	}
}
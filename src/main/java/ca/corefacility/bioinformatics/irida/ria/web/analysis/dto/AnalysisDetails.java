package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Used as a response for encapsulating analysis details
 */

public class AnalysisDetails {
	private String analysisDescription;
	private String workflowName;
	private String version;
	private String priority;
	private Long duration;
	private Date createdDate;
	private AnalysisSubmission.Priority[] priorities;
	private boolean emailPipelineResult;
	private boolean canShareToSamples;
	private boolean updatePermission;
	private boolean updateSamples;

	public AnalysisDetails() {
	}

	public AnalysisDetails(String analysisDescription, String workflowName, String version, String priority, Long duration,
			Date createdDate, AnalysisSubmission.Priority[] priorities, boolean emailPipelineResult,
			boolean canShareToSamples, boolean updatePermission, boolean updateSamples) {
		this.analysisDescription=analysisDescription;
		this.workflowName = workflowName;
		this.version = version;
		this.priority = priority;
		this.duration = duration;
		this.createdDate = createdDate;
		this.priorities = priorities;
		this.emailPipelineResult = emailPipelineResult;
		this.canShareToSamples = canShareToSamples;
		this.updatePermission = updatePermission;
		this.updateSamples = updateSamples;
	}

	public String getAnalysisDescription() {
		return analysisDescription;
	}

	public void setAnalysisDescription(String analysisDescription) {
		this.analysisDescription = analysisDescription;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public AnalysisSubmission.Priority[] getPriorities() {
		return priorities;
	}

	public void setPriorities(AnalysisSubmission.Priority[] priorities) {
		this.priorities = priorities;
	}

	public boolean isEmailPipelineResult() {
		return emailPipelineResult;
	}

	public void setEmailPipelineResult(boolean emailPipelineResult) {
		this.emailPipelineResult = emailPipelineResult;
	}

	public boolean isCanShareToSamples() {
		return canShareToSamples;
	}

	public void setCanShareToSamples(boolean canShareToSamples) {
		this.canShareToSamples = canShareToSamples;
	}

	public boolean isUpdatePermission() {
		return updatePermission;
	}

	public void setUpdatePermission(boolean updatePermission) {
		this.updatePermission = updatePermission;
	}

	public boolean isUpdateSamples() {
		return updateSamples;
	}

	public void setUpdateSamples(boolean updateSamples) {
		this.updateSamples = updateSamples;
	}
}

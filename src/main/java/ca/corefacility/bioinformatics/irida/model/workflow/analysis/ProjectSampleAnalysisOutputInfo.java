package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;

/**
 * Class to store basic {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile} and associated {@link ca.corefacility.bioinformatics.irida.model.sample.Sample}, {@link ca.corefacility.bioinformatics.irida.model.project.Project} and {@link ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission} information from a native SQL query.
 */
public class ProjectSampleAnalysisOutputInfo {
	private Long sampleId;
	private String sampleName;
	private Long analysisId;
	private String analysisOutputFileKey;
	private String filePath;
	private String filename;
	private Long analysisOutputFileId;
	private AnalysisType analysisType;
	private UUID workflowId;
	private Date createdDate;
	private String analysisSubmissionName;
	private Long analysisSubmissionId;
	private Long userId;
	private String userFirstName;
	private String userLastName;
	private IridaWorkflowDescription workflowDescription;
	private Long projectId;

	public ProjectSampleAnalysisOutputInfo() {}

	public ProjectSampleAnalysisOutputInfo(Long sampleId, String sampleName, Long analysisId,
			String analysisOutputFileKey, String filePath, Long analysisOutputFileId, AnalysisType analysisType,
			UUID workflowId, Date createdDate, String analysisSubmissionName, Long analysisSubmissionId, Long userId,
			String userFirstName, String userLastName, Long projectId) {
		this.sampleId = sampleId;
		this.sampleName = sampleName;
		this.analysisId = analysisId;
		this.analysisOutputFileKey = analysisOutputFileKey;
		this.filePath = filePath;
		this.analysisOutputFileId = analysisOutputFileId;
		this.analysisType = analysisType;
		this.workflowId = workflowId;
		this.createdDate = createdDate;
		this.analysisSubmissionName = analysisSubmissionName;
		this.analysisSubmissionId = analysisSubmissionId;
		this.userId = userId;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.workflowDescription = null;
		this.projectId = projectId;
	}

	@Override
	public String toString() {
		return "ProjectSampleAnalysisOutputInfo{" + "sampleId=" + sampleId + ", sampleName='" + sampleName + '\''
				+ ", analysisId=" + analysisId + ", analysisOutputFileKey='" + analysisOutputFileKey + '\''
				+ ", filePath='" + filePath + '\'' + ", analysisOutputFileId=" + analysisOutputFileId
				+ ", analysisType=" + analysisType + ", workflowId=" + workflowId + ", createdDate=" + createdDate
				+ ", analysisSubmissionName='" + analysisSubmissionName + '\'' + ", analysisSubmissionId="
				+ analysisSubmissionId + ", userId=" + userId + ", userFirstName='" + userFirstName + '\''
				+ ", userLastName='" + userLastName + ", projectId='" + projectId +  '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ProjectSampleAnalysisOutputInfo that = (ProjectSampleAnalysisOutputInfo) o;
		return Objects.equals(getSampleId(), that.getSampleId()) && Objects.equals(getSampleName(),
				that.getSampleName()) && Objects.equals(getAnalysisId(), that.getAnalysisId()) && Objects.equals(
				getAnalysisOutputFileKey(), that.getAnalysisOutputFileKey()) && Objects.equals(getFilePath(),
				that.getFilePath()) && Objects.equals(getAnalysisOutputFileId(), that.getAnalysisOutputFileId())
				&& Objects.equals(getAnalysisType(), that.getAnalysisType()) && Objects.equals(getWorkflowId(), that.getWorkflowId())
				&& Objects.equals(getCreatedDate().getTime(), that.getCreatedDate().getTime()) && Objects.equals(
				getAnalysisSubmissionName(), that.getAnalysisSubmissionName()) && Objects.equals(
				getAnalysisSubmissionId(), that.getAnalysisSubmissionId()) && Objects.equals(getUserId(),
				that.getUserId()) && Objects.equals(getUserFirstName(), that.getUserFirstName()) && Objects.equals(
				getUserLastName(), that.getUserLastName()) && Objects.equals(
				getProjectId(), that.getProjectId());
	}

	@Override
	public int hashCode() {

		return Objects.hash(getSampleId(), getSampleName(), getAnalysisId(), getAnalysisOutputFileKey(), getFilePath(),
				getAnalysisOutputFileId(), getAnalysisType(), getWorkflowId(), getCreatedDate(),
				getAnalysisSubmissionName(), getAnalysisSubmissionId(), getUserId(), getUserFirstName(),
				getUserLastName(), getProjectId());
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

	public Long getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}

	public String getAnalysisOutputFileKey() {
		return analysisOutputFileKey;
	}

	public void setAnalysisOutputFileKey(String analysisOutputFileKey) {
		this.analysisOutputFileKey = analysisOutputFileKey;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Long getAnalysisOutputFileId() {
		return analysisOutputFileId;
	}

	public void setAnalysisOutputFileId(Long analysisOutputFileId) {
		this.analysisOutputFileId = analysisOutputFileId;
	}

	public AnalysisType getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

	public UUID getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(UUID workflowId) {
		this.workflowId = workflowId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getAnalysisSubmissionName() {
		return analysisSubmissionName;
	}

	public void setAnalysisSubmissionName(String analysisSubmissionName) {
		this.analysisSubmissionName = analysisSubmissionName;
	}

	public Long getAnalysisSubmissionId() {
		return analysisSubmissionId;
	}

	public void setAnalysisSubmissionId(Long analysisSubmissionId) {
		this.analysisSubmissionId = analysisSubmissionId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public void setWorkflowDescription(IridaWorkflowDescription iridaWorkflowDescription) {
		workflowDescription = iridaWorkflowDescription;
	}

	public IridaWorkflowDescription getWorkflowDescription() {
		return workflowDescription;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}

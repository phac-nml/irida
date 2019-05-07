package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * Represents the update for sharing an analysis with a project.
 */
public class AnalysisProjectShare {
	private Long projectId;
	private boolean shareStatus;

	public AnalysisProjectShare() {
	}

	public AnalysisProjectShare(Long projectId, boolean shareStatus) {
		this.projectId = projectId;
		this.shareStatus = shareStatus;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public boolean isShareStatus() {
		return shareStatus;
	}

	public void setShareStatus(boolean shareStatus) {
		this.shareStatus = shareStatus;
	}
}

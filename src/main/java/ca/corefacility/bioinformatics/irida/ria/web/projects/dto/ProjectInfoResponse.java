package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

/**
 * Encapsulates information about the project as well as permissions.
 */

public class ProjectInfoResponse {
	private Long projectId;
	private String projectName;
	private boolean canManage;
	private boolean canManageRemote;

	public ProjectInfoResponse(Long projectId, String projectName, boolean canManage, boolean canManageRemote) {
		this.projectId = projectId;
		this.projectName = projectName;
		this.canManage = canManage;
		this.canManageRemote = canManageRemote;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public boolean isCanManage() {
		return canManage;
	}

	public void setCanManage(boolean canManage) {
		this.canManage = canManage;
	}

	public boolean isCanManageRemote() {
		return canManageRemote;
	}

	public void setCanManageRemote(boolean canManageRemote) {
		this.canManageRemote = canManageRemote;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote;

import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;

/**
 * Used to handle requests from the UI to update settings for a remote sync'd project. The field is the attribute to be
 * update, and the value is the value to set to that field.
 */

public class RemoteProjectSettingsUpdateRequest {
	private boolean forceSync;
	private boolean markSync;
	private boolean changeUser;
	private ProjectSyncFrequency projectSyncFrequency;

	public RemoteProjectSettingsUpdateRequest(boolean forceSync, boolean markSync, boolean changeUser,
			ProjectSyncFrequency projectSyncFrequency) {
		this.forceSync = forceSync;
		this.markSync = markSync;
		this.changeUser = changeUser;
		this.projectSyncFrequency = projectSyncFrequency;
	}

	public RemoteProjectSettingsUpdateRequest() {
	}

	public boolean getForceSync() {
		return forceSync;
	}

	public void setForceSync(boolean forceSync) {
		this.forceSync = forceSync;
	}

	public boolean getMarkSync() {
		return markSync;
	}

	public void setMarkSync(boolean markSync) {
		this.markSync = markSync;
	}

	public boolean getChangeUser() {
		return changeUser;
	}

	public void setChangeUser(boolean changeUser) {
		this.changeUser = changeUser;
	}

	public ProjectSyncFrequency getProjectSyncFrequency() {
		return projectSyncFrequency;
	}

	public void setProjectSyncFrequency(ProjectSyncFrequency projectSyncFrequency) {
		this.projectSyncFrequency = projectSyncFrequency;
	}
}

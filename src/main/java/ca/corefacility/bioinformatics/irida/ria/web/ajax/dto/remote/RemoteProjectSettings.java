package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Used as a response for encapsulating a remote synchronized project settings
 */

public class RemoteProjectSettings {
	private RemoteStatus remoteStatus;
	private Date lastUpdate;
	private RemoteAPI remoteAPI;
	private ProjectSyncFrequency[] projectSyncFrequencies;
	private ProjectSyncFrequency projectSyncFrequency;
	private User syncUser;

	public RemoteProjectSettings(RemoteStatus remoteStatus, Date lastUpdate, RemoteAPI remoteAPI,
			ProjectSyncFrequency[] projectSyncFrequencies, ProjectSyncFrequency projectSyncFrequency, User syncUser) {
		this.remoteStatus = remoteStatus;
		this.lastUpdate = lastUpdate;
		this.remoteAPI = remoteAPI;
		this.projectSyncFrequencies = projectSyncFrequencies;
		this.projectSyncFrequency = projectSyncFrequency;
		this.syncUser = syncUser;
	}

	public RemoteStatus getRemoteStatus() {
		return remoteStatus;
	}

	public void setRemoteStatus(RemoteStatus remoteStatus) {
		this.remoteStatus = remoteStatus;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public RemoteAPI getRemoteAPI() {
		return remoteAPI;
	}

	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}

	public ProjectSyncFrequency[] getProjectSyncFrequencies() {
		return projectSyncFrequencies;
	}

	public void setProjectSyncFrequencies(ProjectSyncFrequency[] projectSyncFrequencies) {
		this.projectSyncFrequencies = projectSyncFrequencies;
	}

	public ProjectSyncFrequency getProjectSyncFrequency() {
		return projectSyncFrequency;
	}

	public void setProjectSyncFrequency(ProjectSyncFrequency projectSyncFrequency) {
		this.projectSyncFrequency = projectSyncFrequency;
	}

	public User getSyncUser() {
		return syncUser;
	}

	public void setSyncUser(User syncUser) {
		this.syncUser = syncUser;
	}
}

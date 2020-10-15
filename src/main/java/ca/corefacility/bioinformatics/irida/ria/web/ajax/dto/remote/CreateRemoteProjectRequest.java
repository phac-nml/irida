package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote;

import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;

/**
 * Used to capture details about creating a new remote project through the UI
 */
public class CreateRemoteProjectRequest {
	private String url;
	private ProjectSyncFrequency frequency;

	public CreateRemoteProjectRequest(String url, ProjectSyncFrequency frequency) {
		this.url = url;
		this.frequency = frequency;
	}

	public CreateRemoteProjectRequest() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ProjectSyncFrequency getFrequency() {
		return frequency;
	}

	public void setFrequency(ProjectSyncFrequency frequency) {
		this.frequency = frequency;
	}
}
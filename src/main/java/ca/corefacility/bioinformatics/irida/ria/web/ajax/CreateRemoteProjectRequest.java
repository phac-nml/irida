package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;

public class CreateRemoteProjectRequest {
	private String url;
	private int frequency;

	public CreateRemoteProjectRequest(String url, int frequency) {
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
		switch (frequency) {
		case 1:
			return ProjectSyncFrequency.DAILY;
		case 30:
			return ProjectSyncFrequency.MONTHLY;
		case 60:
			return ProjectSyncFrequency.SEMIMONTHLY;
		case 90:
			return ProjectSyncFrequency.QUARTERLY;
		default:
			return ProjectSyncFrequency.WEEKLY;
		}
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}

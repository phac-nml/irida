package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

public class CreateProjectRequest {
	private String name;
	private String organism;
	private String description;
	private String remoteURL;
	private List<Long> samples;
	private Boolean lock;

	public CreateProjectRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemoteURL() {
		return remoteURL;
	}

	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	public List<Long> getSamples() {
		return samples;
	}

	public void setSamples(List<Long> samples) {
		this.samples = samples;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}
}

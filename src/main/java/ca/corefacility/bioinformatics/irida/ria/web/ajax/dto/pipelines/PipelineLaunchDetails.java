package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

public class PipelineLaunchDetails {
	private String name;
	private String description;
	private boolean shareWithProjects;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isShareWithProjects() {
		return shareWithProjects;
	}

	public void setShareWithProjects(boolean shareWithProjects) {
		this.shareWithProjects = shareWithProjects;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * UI Request to create a new metadata template within a project.
 */
public class NewMetadataTemplateRequest {
	private Long projectId;
	private String name;
	private String description;

	public NewMetadataTemplateRequest() {
	}

	public NewMetadataTemplateRequest(Long projectId, String name, String description) {
		this.projectId = projectId;
		this.name = name;
		this.description = description;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

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
}

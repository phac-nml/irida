package ca.corefacility.bioinformatics.irida.ria.web.models;

import java.util.List;

public class UISaveMetadataTemplate {
	private Long projectId;
	private Long id;
	private String name;
	private List<String> fields;

	public UISaveMetadataTemplate() {}

	public UISaveMetadataTemplate(Long projectId, Long id, String name, List<String> fields) {
		this.projectId = projectId;
		this.id = id;
		this.name = name;
		this.fields = fields;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public Long getProjectId() {
		return projectId;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<String> getFields() {
		return fields;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto;

import java.util.Date;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;

/**
 * Used to represent a project metadata template in the UI.
 * A special class was required for this since it needs the project specific metadata fields
 * that include the permissions for that field on that project.
 */
public class ProjectMetadataTemplate {
	private Long identifier;
	private String name;
	private String description;
	private Date createdDate;
	private Date modifiedDate;
	private List<ProjectMetadataField> fields;

	public ProjectMetadataTemplate(MetadataTemplate template, List<ProjectMetadataField> fields) {
		this.identifier = template.getId();
		this.name = template.getName();
		this.description = template.getDescription();
		this.createdDate = template.getCreatedDate();
		this.modifiedDate = template.getModifiedDate();
		this.fields = fields;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<ProjectMetadataField> getFields() {
		return fields;
	}

	public void setFields(List<ProjectMetadataField> fields) {
		this.fields = fields;
	}
}

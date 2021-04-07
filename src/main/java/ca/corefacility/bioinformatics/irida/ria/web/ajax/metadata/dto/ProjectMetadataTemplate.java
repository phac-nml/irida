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
	private Long id;
	private String label;
	private String description;
	private Date createdDate;
	private Date modifiedDate;
	private List<ProjectMetadataField> fields;

	public ProjectMetadataTemplate(MetadataTemplate template, List<ProjectMetadataField> fields) {
		this.id = template.getId();
		this.label = template.getLabel();
		this.description = template.getDescription();
		this.createdDate = template.getCreatedDate();
		this.modifiedDate = template.getModifiedDate();
		this.fields = fields;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

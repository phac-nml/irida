package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

/**
 * A representation of a {@link MetadataTemplateField} specifically for a project. This class was required since
 * metadata fields for a project need to include the restriction level specific for that project.
 */
public class ProjectMetadataField {
	private Long id;
	private String fieldKey;
	private String label;
	private String type;
	private String restriction;

	//default constructor for serializing and deserializing the DTO
	public ProjectMetadataField() {
	}

	public ProjectMetadataField(MetadataTemplateField field, String restriction) {
		this.id = field.getId();
		this.fieldKey = field.getFieldKey();
		this.label = field.getLabel();
		this.type = field.getType();
		this.restriction = restriction;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFieldKey() {
		return fieldKey;
	}

	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRestriction() {
		return restriction;
	}

	public void setRestriction(String restriction) {
		this.restriction = restriction;
	}

}

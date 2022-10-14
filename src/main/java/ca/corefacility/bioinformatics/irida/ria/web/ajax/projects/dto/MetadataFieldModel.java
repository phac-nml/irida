package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto;

/**
 * Model for UI to represent a metadata field.
 */
public class MetadataFieldModel {

	private String field;

	private String value;

	private String restriction;

	public MetadataFieldModel(String field, String value, String restriction) {
		this.field = field;
		this.value = value;
		this.restriction = restriction;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRestriction() {
		return restriction;
	}

	public void setRestriction(String restriction) {
		this.restriction = restriction;
	}
}

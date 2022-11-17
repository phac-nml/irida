package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto;

/**
 * Model for UI to represent a metadata entry.
 */
public class MetadataEntryModel {

	private String field;

	private String value;

	public MetadataEntryModel(String field, String value) {
		this.field = field;
		this.value = value;
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

}

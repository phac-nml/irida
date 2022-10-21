package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * Used by UI when any field needs to have its value updated.
 */
public class FieldUpdate {
	/*
	Attribute on the object to update
	 */
	private String field;

	/*
	New value to set
	 */
	private String value;

	public FieldUpdate() {
	}

	public FieldUpdate(String field, String value) {
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

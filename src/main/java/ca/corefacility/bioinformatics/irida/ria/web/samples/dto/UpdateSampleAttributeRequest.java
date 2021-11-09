package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

/**
 * Used to handle requests from the UI to update a specific attribute on a sample.
 * The field is the attribute to be update, and the value is the value to set to that field.
 */
public class UpdateSampleAttributeRequest {
	private String field;
	private String value;

	public UpdateSampleAttributeRequest() {}

	public UpdateSampleAttributeRequest(String field, String value) {
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
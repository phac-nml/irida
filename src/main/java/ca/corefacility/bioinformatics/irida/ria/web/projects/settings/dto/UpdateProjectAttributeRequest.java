package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

public class UpdateProjectAttributeRequest {
	private String field;
	private String value;

	public UpdateProjectAttributeRequest() {
	}

	public UpdateProjectAttributeRequest(String field, String value) {
		this.field = field;
		this.value = value;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getField() {
		return field;
	}

	public String getValue() {
		return value;
	}
}

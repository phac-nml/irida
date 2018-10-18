package ca.corefacility.bioinformatics.irida.ria.web.linelist.dto;

/**
 * Class to represent any linelist field that cannot be modified in the UI.
 */
public class UIStaticField {
	private String field;
	private String type;

	public UIStaticField(String field, String type) {
		this.field = field;
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public String getType() {
		return type;
	}
}

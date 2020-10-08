package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * Represents a user group role in the UI.
 */
public class UserGroupRole {
	private final String label;
	private final String value;

	public UserGroupRole(String value, String label) {
		this.label = label;
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}
}

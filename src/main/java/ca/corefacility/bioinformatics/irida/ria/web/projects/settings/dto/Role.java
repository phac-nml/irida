package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

/**
 * Represents a {@link ca.corefacility.bioinformatics.irida.model.enums.ProjectRole} in the interface,
 * including it's translation.
 */
public class Role {
	/*
	The enum value of the ProjectRole
	 */
	private final String value;

	/*
	The internationalized label
	 */
	private final String label;

	public Role(String value, String label) {
		this.value = value;
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		return label;
	}
}

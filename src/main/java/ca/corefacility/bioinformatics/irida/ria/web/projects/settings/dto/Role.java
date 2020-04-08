package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

public class Role {
	private String value;
	private String label;

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

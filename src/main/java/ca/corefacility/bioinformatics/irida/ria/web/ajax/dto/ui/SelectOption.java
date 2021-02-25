package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui;

/**
 * Generically represents a UI Select Option
 */
public class SelectOption {
	private final String value;
	private final String label;

	public SelectOption(String value, String label) {
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
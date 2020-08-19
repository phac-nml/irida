package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.generic;

public class LabelAndValue {
	private final String label;
	private final String value;

	public LabelAndValue(String label, String value) {
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

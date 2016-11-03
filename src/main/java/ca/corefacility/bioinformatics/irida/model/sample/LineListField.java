package ca.corefacility.bioinformatics.irida.model.sample;

/**
 * Describes an individual field in a linelist.
 */
public class LineListField {
	private String label;
	private String type;

	public LineListField(String label, String type) {
		this.label = label;
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public String getType() {
		return type;
	}
}

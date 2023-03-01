package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui;

/**
 * Used in the UI to render a Irida Workflow Pipeline Parameter option
 */
public class Input {
	private String label;
	private String name;
	private String value;

	public Input() {
	}

	public Input(String name, String label, String value) {
		this.label = label;
		this.name = name;
		this.value = value;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

public class WorkflowState {
	private String text;
	private String value;

	public WorkflowState(String text, String value) {
		this.text = text;
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public String getValue() {
		return value;
	}
}

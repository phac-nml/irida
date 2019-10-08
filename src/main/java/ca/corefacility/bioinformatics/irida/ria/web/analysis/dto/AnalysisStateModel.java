package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * UI Representation of an Analysis Submission state.
 */
public class AnalysisStateModel {
	/*
	Internationalized label
	 */
	private String text;
	/*
	Acutal enum value for the state.
	 */
	private String value;

	public AnalysisStateModel(String text, String value) {
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

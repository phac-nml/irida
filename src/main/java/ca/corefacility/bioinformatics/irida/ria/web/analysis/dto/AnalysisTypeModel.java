package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

/**
 * UI Representation of an Analysis Type
 */
public class AnalysisTypeModel {
	/*
	Internationalized type
	 */
	private String text;

	/*
	Actual value for the type.
	 */
	private String value;

	public AnalysisTypeModel(String text, String value) {
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

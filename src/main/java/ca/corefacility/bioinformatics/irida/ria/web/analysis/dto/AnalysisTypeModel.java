package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

public class AnalysisTypeModel {
	private String text;
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

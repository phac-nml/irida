package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;

public class AnalysesFilters {
	private AnalysisState state;
	private String type;

	public void setState(String s) {
		this.state = AnalysisState.valueOf(s);
	}

	public AnalysisState getState() {
		return state;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}

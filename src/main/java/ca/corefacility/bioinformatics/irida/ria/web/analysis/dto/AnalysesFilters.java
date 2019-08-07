package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.ArrayList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;

public class AnalysesFilters {
	private AnalysisState state;
	private List<String> type = new ArrayList();

	public void setState(String s) {
		this.state = AnalysisState.valueOf(s);
	}

	public AnalysisState getState() {
		return state;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public List<String> getType() {
		return type;
	}
}

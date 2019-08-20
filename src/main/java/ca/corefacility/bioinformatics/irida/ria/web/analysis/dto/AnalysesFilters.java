package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;

/**
 * Represents the filters on the Analyses Table.
 */
public class AnalysesFilters {
	private List<AnalysisState> state = new ArrayList<>();
	private List<String> type = new ArrayList<>();

	public void setState(List<String> stateStrings) {
		this.state = stateStrings.stream().map(AnalysisState::valueOf).collect(Collectors.toList());
	}

	public List<AnalysisState> getState() {
		return state;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public List<String> getType() {
		return type;
	}
}

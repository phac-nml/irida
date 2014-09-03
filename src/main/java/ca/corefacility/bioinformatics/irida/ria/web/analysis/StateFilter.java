package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;

/**
 * Class for conversion from JSON.  This class is used to get the
 * state filter value.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class StateFilter {

	AnalysisState value;
	String text;

	public AnalysisState getState() {
		return value;
	}

	public void setValue(String value) {
		this.value = AnalysisState.fromString(value);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

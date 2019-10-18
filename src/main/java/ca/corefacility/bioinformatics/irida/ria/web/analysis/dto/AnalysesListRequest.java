package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import ca.corefacility.bioinformatics.irida.ria.web.models.TableRequest;

/**
 * Used to handle a UI Request for the Analyses Page.
 * This is the basic information required to create the table.
 */
public class AnalysesListRequest extends TableRequest {
	private AnalysesFilters filters;

	public AnalysesListRequest() {}

	public AnalysesFilters getFilters() {
		return filters;
	}

	public void setFilters(AnalysesFilters filters) {
		this.filters = filters;
	}
}

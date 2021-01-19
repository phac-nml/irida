package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * UI Response to to encapsulate statistics.
 */

public class StatisticsResponse extends AjaxResponse {
	private List<GenericStatModel> statistics;

	public StatisticsResponse(List<GenericStatModel> statistics) {
		this.statistics = statistics;
	}

	public List<GenericStatModel> getStatistics() {
		return statistics;
	}

	public void setStatistics(List<GenericStatModel> statistics) {
		this.statistics = statistics;
	}
}

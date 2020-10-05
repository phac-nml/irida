package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

import java.util.List;

/**
 * UI Response to to encapsulate analyses statistics.
 */

public class AnalysesStatsResponse {
	private List<GenericStatModel> analysesStats;

	public AnalysesStatsResponse(List<GenericStatModel> analysesStats) {
		this.analysesStats = analysesStats;
	}

	public List<GenericStatModel> getAnalysesStats() {
		return analysesStats;
	}

	public void setAnalysesStats(List<GenericStatModel> analysesStats) {
		this.analysesStats = analysesStats;
	}
}

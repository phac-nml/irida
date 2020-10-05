package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

import java.util.List;

/**
 * UI Response to to encapsulate sample statistics.
 */

public class SampleStatsResponse {
	private List<GenericStatModel> sampleStats;

	public SampleStatsResponse(List<GenericStatModel> sampleStats) {
		this.sampleStats = sampleStats;
	}

	public List<GenericStatModel> getSampleStats() {
		return sampleStats;
	}

	public void setSampleStats(List<GenericStatModel> sampleStats) {
		this.sampleStats = sampleStats;
	}
}

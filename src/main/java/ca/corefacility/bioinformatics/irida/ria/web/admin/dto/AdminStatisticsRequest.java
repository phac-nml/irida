package ca.corefacility.bioinformatics.irida.ria.web.admin.dto;

/**
 * Used by the UI to to retrieve usage stats for
 * projects, analyses, samples, and users.
 */

public class AdminStatisticsRequest {
	private AdvancedStats advancedStats;
	private BasicStats basicStats;

	public AdminStatisticsRequest(AdvancedStats advancedStats, BasicStats basicStats) {
		this.advancedStats = advancedStats;
		this.basicStats = basicStats;
	}

	public AdvancedStats getAdvancedStats() {
		return advancedStats;
	}

	public void setAdvancedStats(AdvancedStats advancedStats) {
		this.advancedStats = advancedStats;
	}

	public BasicStats getBasicStats() {
		return basicStats;
	}

	public void setBasicStats(BasicStats basicStats) {
		this.basicStats = basicStats;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

import java.util.List;

/**
 * UI Response to to encapsulate user statistics.
 */

public class UserStatsResponse {
	private List<GenericStatModel> userStats;

	public UserStatsResponse(List<GenericStatModel> userStats) {
		this.userStats = userStats;
	}

	public List<GenericStatModel> getUserStats() {
		return userStats;
	}

	public void setUserStats(List<GenericStatModel> userStats) {
		this.userStats = userStats;
	}
}

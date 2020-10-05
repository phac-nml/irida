package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

import java.util.List;

/**
 * UI Response to to encapsulate project statistics.
 */

public class ProjectStatsResponse {
	private List<GenericStatModel> projectStats;

	public ProjectStatsResponse(List<GenericStatModel> projectStats) {
		this.projectStats = projectStats;
	}

	public List<GenericStatModel> getProjectStats() {
		return projectStats;
	}

	public void setProjectStats(List<GenericStatModel> projectStats) {
		this.projectStats = projectStats;
	}
}

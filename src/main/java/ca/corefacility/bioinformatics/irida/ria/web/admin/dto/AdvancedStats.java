package ca.corefacility.bioinformatics.irida.ria.web.admin.dto;

import java.util.List;

public class AdvancedStats {
	private List<GenericStatModel> analysesStats;
	private List<GenericStatModel> projectStats;
	private List<GenericStatModel> sampleStats;
	private List<GenericStatModel> userStats;

	public AdvancedStats(List<GenericStatModel> analysesStats, List<GenericStatModel> projectStats,
			List<GenericStatModel> sampleStats, List<GenericStatModel> userStats) {
		this.analysesStats = analysesStats;
		this.projectStats = projectStats;
		this.sampleStats = sampleStats;
		this.userStats = userStats;
	}

	public List<GenericStatModel> getAnalysesStats() {
		return analysesStats;
	}

	public void setAnalysesStats(List<GenericStatModel> analysesStats) {
		this.analysesStats = analysesStats;
	}

	public List<GenericStatModel> getProjectStats() {
		return projectStats;
	}

	public void setProjectStats(List<GenericStatModel> projectStats) {
		this.projectStats = projectStats;
	}

	public List<GenericStatModel> getSampleStats() {
		return sampleStats;
	}

	public void setSampleStats(List<GenericStatModel> sampleStats) {
		this.sampleStats = sampleStats;
	}

	public List<GenericStatModel> getUserStats() {
		return userStats;
	}

	public void setUserStats(List<GenericStatModel> userStats) {
		this.userStats = userStats;
	}
}

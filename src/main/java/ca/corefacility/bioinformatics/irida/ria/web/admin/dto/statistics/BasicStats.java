package ca.corefacility.bioinformatics.irida.ria.web.admin.dto.statistics;

import java.util.List;

/**
 * UI Response to to encapsulate basic usage statistics.
 */
public class BasicStats {
	private Long analysesRan;
	private Long projectsCreated;
	private Long samplesCreated;
	private Long usersCreated;
	private Long usersLoggedIn;
	private List<Long> analysesStats;
	private List<Long> projectStats;
	private List<Long> sampleStats;
	private List<Long> userStats;

	public BasicStats(Long analysesRan, Long projectsCreated, Long samplesCreated, Long usersCreated,
			Long usersLoggedIn, List<Long> analysesStats, List<Long> projectStats, List<Long> sampleStats,
			List<Long> userStats) {
		this.analysesRan = analysesRan;
		this.projectsCreated = projectsCreated;
		this.samplesCreated = samplesCreated;
		this.usersCreated = usersCreated;
		this.usersLoggedIn = usersLoggedIn;
		this.analysesStats = analysesStats;
		this.projectStats = projectStats;
		this.sampleStats = sampleStats;
		this.userStats = userStats;
	}

	public Long getAnalysesRan() {
		return analysesRan;
	}

	public void setAnalysesRan(Long analysesRan) {
		this.analysesRan = analysesRan;
	}

	public Long getProjectsCreated() {
		return projectsCreated;
	}

	public void setProjectsCreated(Long projectsCreated) {
		this.projectsCreated = projectsCreated;
	}

	public Long getSamplesCreated() {
		return samplesCreated;
	}

	public void setSamplesCreated(Long samplesCreated) {
		this.samplesCreated = samplesCreated;
	}

	public Long getUsersCreated() {
		return usersCreated;
	}

	public void setUsersCreated(Long usersCreated) {
		this.usersCreated = usersCreated;
	}

	public Long getUsersLoggedIn() {
		return usersLoggedIn;
	}

	public void setUsersLoggedIn(Long usersLoggedIn) {
		this.usersLoggedIn = usersLoggedIn;
	}

	public List<Long> getAnalysesStats() {
		return analysesStats;
	}

	public void setAnalysesStats(List<Long> analysesStats) {
		this.analysesStats = analysesStats;
	}

	public List<Long> getProjectStats() {
		return projectStats;
	}

	public void setProjectStats(List<Long> projectStats) {
		this.projectStats = projectStats;
	}

	public List<Long> getSampleStats() {
		return sampleStats;
	}

	public void setSampleStats(List<Long> sampleStats) {
		this.sampleStats = sampleStats;
	}

	public List<Long> getUserStats() {
		return userStats;
	}

	public void setUserStats(List<Long> userStats) {
		this.userStats = userStats;
	}
}

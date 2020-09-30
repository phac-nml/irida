package ca.corefacility.bioinformatics.irida.ria.web.admin.dto;

/**
 * Used by the UI to to retrieve usage stats for
 * projects, analyses, samples, and users.
 */

public class AdminStatisticsRequest {
	private AnalysesStatistics analysesStatistics;
	private ProjectStatistics projectStatistics;
	private SampleStatistics sampleStatistics;
	private UserStatistics userStatistics;

	public AdminStatisticsRequest(AnalysesStatistics analysesStatistics, ProjectStatistics projectStatistics,
			SampleStatistics sampleStatistics, UserStatistics userStatistics) {
		this.analysesStatistics = analysesStatistics;
		this.projectStatistics = projectStatistics;
		this.sampleStatistics = sampleStatistics;
		this.userStatistics = userStatistics;
	}

	public AnalysesStatistics getAnalysesStatistics() {
		return analysesStatistics;
	}

	public void setAnalysesStatistics(AnalysesStatistics analysesStatistics) {
		this.analysesStatistics = analysesStatistics;
	}

	public ProjectStatistics getProjectStatistics() {
		return projectStatistics;
	}

	public void setProjectStatistics(ProjectStatistics projectStatistics) {
		this.projectStatistics = projectStatistics;
	}

	public SampleStatistics getSampleStatistics() {
		return sampleStatistics;
	}

	public void setSampleStatistics(SampleStatistics sampleStatistics) {
		this.sampleStatistics = sampleStatistics;
	}

	public UserStatistics getUserStatistics() {
		return userStatistics;
	}

	public void setUserStatistics(UserStatistics userStatistics) {
		this.userStatistics = userStatistics;
	}
}

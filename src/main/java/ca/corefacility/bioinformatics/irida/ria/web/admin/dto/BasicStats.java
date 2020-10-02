package ca.corefacility.bioinformatics.irida.ria.web.admin.dto;

public class BasicStats {
	private Long analysesRan;
	private Long projectsCreated;
	private Long samplesCreated;
	private Long usersLoggedIn;

	public BasicStats(Long analysesRan, Long projectsCreated, Long samplesCreated, Long usersLoggedIn) {
		this.analysesRan = analysesRan;
		this.projectsCreated = projectsCreated;
		this.samplesCreated = samplesCreated;
		this.usersLoggedIn = usersLoggedIn;
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

	public Long getUsersLoggedIn() {
		return usersLoggedIn;
	}

	public void setUsersLoggedIn(Long usersLoggedIn) {
		this.usersLoggedIn = usersLoggedIn;
	}
}

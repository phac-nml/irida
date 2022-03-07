package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

public class UserStatisticsResponse {
	int numberOfProjects;
	int numberOfSamples;
	int numberOfAnalyses;

	public UserStatisticsResponse(int numberOfProjects, int numberOfSamples, int numberOfAnalyses) {
		this.numberOfProjects = numberOfProjects;
		this.numberOfSamples = numberOfSamples;
		this.numberOfAnalyses = numberOfAnalyses;
	}

	public int getNumberOfProjects() {
		return numberOfProjects;
	}

	public void setNumberOfProjects(int numberOfProjects) {
		this.numberOfProjects = numberOfProjects;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public void setNumberOfSamples(int numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	public int getNumberOfAnalyses() {
		return numberOfAnalyses;
	}

	public void setNumberOfAnalyses(int numberOfAnalyses) {
		this.numberOfAnalyses = numberOfAnalyses;
	}
}

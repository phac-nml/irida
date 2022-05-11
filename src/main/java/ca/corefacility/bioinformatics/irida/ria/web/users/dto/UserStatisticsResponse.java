package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

/**
 * UI Response to to encapsulate user statistics.
 */
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

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public int getNumberOfAnalyses() {
		return numberOfAnalyses;
	}

}

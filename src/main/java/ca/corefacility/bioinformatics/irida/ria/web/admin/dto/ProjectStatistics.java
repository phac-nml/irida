package ca.corefacility.bioinformatics.irida.ria.web.admin.dto;

/**
 * Used by the UI to to get updated project statistics.
 */

public class ProjectStatistics {
	private Long numProjects;

	public ProjectStatistics(Long numProjects) {
		this.numProjects = numProjects;
	}

	public Long getNumProjects() {
		return numProjects;
	}

	public void setNumProjects(Long numProjects) {
		this.numProjects = numProjects;
	}
}

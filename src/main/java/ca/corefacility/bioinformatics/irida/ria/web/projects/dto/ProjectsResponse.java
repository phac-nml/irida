package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

/**
 * Contains information needed for listing the users or administrators projects
 * on within the UI Projects table,
 */
public class ProjectsResponse {
	private List<ProjectModel> projects;
	private Long total;

	public ProjectsResponse(List<ProjectModel> projects, long total) {
		this.projects = projects;
		this.total = total;
	}

	public List<ProjectModel> getProjects() {
		return projects;
	}

	public Long getTotal() {
		return total;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

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

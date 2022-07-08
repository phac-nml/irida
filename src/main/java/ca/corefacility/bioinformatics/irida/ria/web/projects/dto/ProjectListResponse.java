package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Return a list of projects.
 */
public class ProjectListResponse extends AjaxResponse {

	private List<ProjectListItemModel> projects;

	public ProjectListResponse(List<ProjectListItemModel> projects) {
		this.projects = projects;
	}

	public List<ProjectListItemModel> getProjects() {
		return projects;
	}

}

package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Return a list of projects.
 */
public class ProjectNameListResponse extends AjaxResponse {

	private List<ProjectNameListItemModel> projects;

	public ProjectNameListResponse(List<ProjectNameListItemModel> projects) {
		this.projects = projects;
	}

	public List<ProjectNameListItemModel> getProjects() {
		return projects;
	}

}

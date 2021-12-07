package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * UI response for user projects details.
 */
public class UserProjectDetailsResponse extends AjaxResponse {

	private List<UserProjectDetailsModel> projects;

	public UserProjectDetailsResponse(List<UserProjectDetailsModel> projects) {
		this.projects = projects;
	}

	public List<UserProjectDetailsModel> getProjects() {
		return projects;
	}

	public void setProjects(List<UserProjectDetailsModel> projects) {
		this.projects = projects;
	}
}

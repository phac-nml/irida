package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

public class NewProjectGroupRequest {
	/*
	Group Identifier
	 */
	private Long id;

	/*
	Role to add the group to the project as
	 */
	private String role;

	public NewProjectGroupRequest() {
	}

	public NewProjectGroupRequest(Long id, String role) {
		this.id = id;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}

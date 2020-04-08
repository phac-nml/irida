package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

public class NewProjectMemberRequest {
	private Long id;
	private String role;

	public NewProjectMemberRequest() {
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

package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * Used to send information to the server about a user to be added as a project member
 */
public class NewMemberRequest {
	/*
	User Identifier
	 */
	private Long id;

	/*
	Role to add the user to the project as
	 */
	private String projectRole;

	/*
	Project metadata role to assign to new user user
	 */
	private String metadataRole;

	public NewMemberRequest() {
	}

	public NewMemberRequest(Long id, String projectRole, String metadataRole) {
		this.id = id;
		this.projectRole = projectRole;
		this.metadataRole = metadataRole;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProjectRole() {
		return projectRole;
	}

	public void setProjectRole(String projectRole) {
		this.projectRole = projectRole;
	}

	public String getMetadataRole() {
		return metadataRole;
	}

	public void setMetadataRole(String metadataRole) {
		this.metadataRole = metadataRole;
	}
}

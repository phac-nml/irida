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
	private String role;

	private String metadataRole;

	public NewMemberRequest() {
	}

	public NewMemberRequest(Long id, String role, String metadataRole) {
		this.id = id;
		this.role = role;
		this.metadataRole = metadataRole;
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

	public String getMetadataRole() {
		return metadataRole;
	}

	public void setMetadataRole(String metadataRole) {
		this.metadataRole = metadataRole;
	}
}

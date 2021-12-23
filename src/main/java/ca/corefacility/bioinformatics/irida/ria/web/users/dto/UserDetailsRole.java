package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

/**
 * Stores role info the User Details Page.
 */
public class UserDetailsRole {
	public String code;
	public String name;

	public UserDetailsRole(String role, String name) {
		this.code = role;
		this.name = name;
	}

	public String getRole() {
		return code;
	}

	public void setRole(String role) {
		this.code = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

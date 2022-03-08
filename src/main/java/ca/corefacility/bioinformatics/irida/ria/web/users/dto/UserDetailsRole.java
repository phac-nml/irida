package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsRole that = (UserDetailsRole) o;
		return Objects.equals(code, that.code) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, name);
	}
}

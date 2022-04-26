package ca.corefacility.bioinformatics.irida.ria.web.settings.dto;

import java.util.Objects;

/**
 * Stores role info.
 */
public class Role {
	public String code;
	public String name;

	public Role(String role, String name) {
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
		Role that = (Role) o;
		return Objects.equals(code, that.code) && Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, name);
	}
}

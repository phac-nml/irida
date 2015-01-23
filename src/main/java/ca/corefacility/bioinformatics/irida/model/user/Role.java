package ca.corefacility.bioinformatics.irida.model.user;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

/**
 * Roles for authorization in the application.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "system_role")
@Audited
public class Role implements Comparable<Role>, GrantedAuthority {
	private static final long serialVersionUID = 7595149386708058927L;

	/**
	 * Constant reference for administrative role.
	 */
	public static final Role ROLE_ANONYMOUS = new Role("ROLE_ANONYMOUS");

	/**
	 * Constant reference for administrative role.
	 */
	public static final Role ROLE_ADMIN = new Role("ROLE_ADMIN");
	/**
	 * Constant reference for user role.
	 */
	public static final Role ROLE_USER = new Role("ROLE_USER");

	/**
	 * Constant reference for the manager role
	 */
	public static final Role ROLE_MANAGER = new Role("ROLE_MANAGER");

	/**
	 * Constant reference for the sequencer role.
	 */
	public static final Role ROLE_SEQUENCER = new Role("ROLE_SEQUENCER");

	@Id
	private String name;

	@NotNull
	private String description;

	private Role() {
	}

	private Role(String name) {
		this();
		this.name = name;
	}

	private Role(String name, String description) {
		this(name);
		this.description = description;
	}

	@Override
	public int compareTo(Role r) {
		return name.compareTo(r.name);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Role) {
			Role r = (Role) other;
			return Objects.equals(name, r.name);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getAuthority() {
		return name;
	}

	/**
	 * Return a {@link Role} for the given string value
	 * 
	 * @param value
	 *            The string value to create a {@link Role} for
	 * @return A new {@link Role} instance for the given string value
	 */
	public static Role valueOf(String value) {
		return new Role(value);
	}

	@Override
	public String toString() {
		return name;
	}

}

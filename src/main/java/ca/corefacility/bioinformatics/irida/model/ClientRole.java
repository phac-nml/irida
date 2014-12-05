package ca.corefacility.bioinformatics.irida.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

import ca.corefacility.bioinformatics.irida.model.user.Role;

/**
 * Role of an OAuth2 client in the Irida system
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "client_role")
@Audited
public class ClientRole implements GrantedAuthority, Comparable<ClientRole> {

	private static final long serialVersionUID = -5872715742283126858L;

	/**
	 * Constant reference for the OAuth2 client role
	 */
	public static final ClientRole ROLE_CLIENT = new ClientRole("ROLE_CLIENT");

	@Id
	private String name;

	@NotNull
	private String description;

	private ClientRole() {
	}

	private ClientRole(String name) {
		this.name = name;
	}

	private ClientRole(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
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
	public static ClientRole valueOf(String value) {
		return new ClientRole(value);
	}

	@Override
	public int compareTo(ClientRole r) {
		return name.compareTo(r.name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClientRole) {
			ClientRole r = (ClientRole) obj;
			return Objects.equals(name, r.name);
		}

		return false;
	}

}

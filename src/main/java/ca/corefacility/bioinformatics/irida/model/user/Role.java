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
 */
public enum Role implements Comparable<Role>, GrantedAuthority {

	/**
	 * Constant reference for anonymous role.
	 */
	ROLE_ANONYMOUS("ROLE_ANONYMOUS"),

	/**
	 * Constant reference for administrative role.
	 */
	ROLE_ADMIN("ROLE_ADMIN"),
	/**
	 * Constant reference for user role.
	 */
	ROLE_USER("ROLE_USER"),

	/**
	 * Constant reference for the manager role
	 */
	ROLE_MANAGER("ROLE_MANAGER"),

	/**
	 * Constant reference for the sequencer role.
	 */
	ROLE_SEQUENCER("ROLE_SEQUENCER"),
	
	/**
	 * Constant reference for technician role.
	 */
	ROLE_TECHNICIAN("ROLE_TECHNICIAN");



	private String name;


	private Role() {
	}

	private Role(String name) {
		this();
		this.name = name;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getAuthority() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}

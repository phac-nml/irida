package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Details about the currently logged in user for use by the UI.
 */
public class CurrentUser {
	private Long identifier;
	private String username;
	private String firstName;
	private String lastName;
	private boolean isAdmin;
	private boolean isManager;
	private boolean isTechnician;

	public CurrentUser(User user) {
		this.identifier = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.isAdmin = user.getSystemRole().equals(Role.ROLE_ADMIN);
		this.isManager = user.getSystemRole().equals(Role.ROLE_MANAGER);
		this.isTechnician = user.getSystemRole().equals(Role.ROLE_TECHNICIAN);
	}

	public Long getIdentifier() {
		return identifier;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public boolean isManager() {
		return isManager;
	}

	public boolean isTechnician() {
		return isTechnician;
	}
}
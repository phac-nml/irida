package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Describes a {@link User} in a ant.design table.
 */
public class UserDetailsModel extends TableModel {
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String username;
	private String role;
	private Date lastLogin;
	private boolean enabled;
	private String locale;

	public UserDetailsModel(User user) {
		super(user.getId(), user.getUsername(), user.getCreatedDate(), user.getModifiedDate());
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.phoneNumber = user.getPhoneNumber();
		this.username = user.getUsername();
		this.role = user.getSystemRole()
				.getName();
		this.lastLogin = user.getLastLogin();
		this.enabled = user.isEnabled();
		this.locale = user.getLocale();
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getLocale() {
		return locale;
	}
}

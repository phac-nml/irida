package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Date;
import java.util.Objects;

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
	private String type;
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
		this.role = user.getSystemRole().getName();
		this.type = user.getUserType().getName();
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

	public String getType() { return type; }

	public Date getLastLogin() {
		return lastLogin;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getLocale() {
		return locale;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsModel that = (UserDetailsModel) o;
		return enabled == that.enabled && Objects.equals(firstName, that.firstName) && Objects.equals(lastName,
				that.lastName) && Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber)
				&& Objects.equals(username, that.username) && Objects.equals(role, that.role)
				&& Objects.equals(type, that.type) && Objects.equals(lastLogin, that.lastLogin)
				&& Objects.equals(locale, that.locale);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, email, phoneNumber, username, role, type, lastLogin, enabled, locale);
	}
}

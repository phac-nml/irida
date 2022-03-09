package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

/**
 * Data transfer object for editing user details.
 */
public class UserEditRequest {
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String systemRole;
	private String userLocale;
	private String enabled;

	public UserEditRequest() {

	}

	public UserEditRequest(String firstName, String lastName, String email, String phoneNumber, String systemRole,
			String userLocale, String enabled) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.systemRole = systemRole;
		this.userLocale = userLocale;
		this.enabled = enabled;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSystemRole() {
		return systemRole;
	}

	public void setSystemRole(String systemRole) {
		this.systemRole = systemRole;
	}

	public String getUserLocale() {
		return userLocale;
	}

	public void setUserLocale(String userLocale) {
		this.userLocale = userLocale;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
}
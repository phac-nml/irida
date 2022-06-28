package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

/**
 * Data transfer object for editing user details.
 */
public class UserEditRequest {
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String role;
	private String locale;
	private boolean enabled;

	public UserEditRequest(String firstName, String lastName, String email, String phoneNumber, String role,
			String locale, boolean enabled) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.locale = locale;
		this.enabled = enabled;
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

	public String getRole() {
		return role;
	}

	public String getLocale() {
		return locale;
	}

	public boolean getEnabled() {
		return enabled;
	}
}
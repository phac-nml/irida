package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

/**
 * Data transfer object for creating a new user account.
 */
public class UserCreateRequest {
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String role;
	private String locale;
	private String activate;
	private String password;

	public UserCreateRequest(String username, String firstName, String lastName, String email, String phoneNumber,
			String role, String locale, String activate, String password) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.role = role;
		this.locale = locale;
		this.activate = activate;
		this.password = password;
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

	public String getActivate() {
		return activate;
	}

	public String getPassword() {
		return password;
	}

}
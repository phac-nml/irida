package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

/**
 * Stores an error message for a form field on the User Details Page.
 */
public class UserDetailsError {
	private String field;
	private String message;

	public UserDetailsError(String field, String message) {
		this.field = field;
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.dto;

/**
 * Used as a response for ajax result
 */

public class ResponseDetails {
	private String message;

	public ResponseDetails() {
	}

	public ResponseDetails(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
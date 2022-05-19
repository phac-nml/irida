package ca.corefacility.bioinformatics.irida.ria.web.users.dto;

import java.util.Map;

public class PasswordResetResponse {
	private String statusMessage;
	private Map<String, String> errors;

	public PasswordResetResponse(String statusMessage, Map<String, String> errors) {
		this.statusMessage = statusMessage;
		this.errors = errors;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}
}

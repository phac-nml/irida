package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * UI response to update an existing sample
 */
public class UpdateSampleResponse {
	private boolean error;
	private String errorMessage;

	public UpdateSampleResponse() {
	}

	public UpdateSampleResponse(boolean error) {
		this.error = error;
	}

	public UpdateSampleResponse(boolean error, String errorMessage) {
		this.error = error;
		this.errorMessage = errorMessage;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}

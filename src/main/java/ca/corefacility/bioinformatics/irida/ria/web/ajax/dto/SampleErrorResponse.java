package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * UI response to create/update a sample with error
 */
public class SampleErrorResponse {
	private boolean error;
	private String errorMessage;

	public SampleErrorResponse() {
	}

	public SampleErrorResponse(boolean error) {
		this.error = error;
	}

	public SampleErrorResponse(boolean error, String errorMessage) {
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

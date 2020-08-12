package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * Response returned if their is an error creating a new sample in a project.
 */
public class CreateSampleErrorResponse extends CreateSampleResponse {
	private final String error;

	public CreateSampleErrorResponse(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}

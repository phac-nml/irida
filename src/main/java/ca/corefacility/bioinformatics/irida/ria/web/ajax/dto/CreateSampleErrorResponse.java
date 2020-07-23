package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

public class CreateSampleErrorResponse extends CreateSampleResponse {
	private final String error;

	public CreateSampleErrorResponse(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}

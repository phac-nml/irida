package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

public class CreateSampleSuccessResponse extends CreateSampleResponse {
	private final long id;

	public CreateSampleSuccessResponse(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}

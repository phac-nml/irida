package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * UI Response for successfully creating a sample in a project.
 */
public class CreateSampleSuccessResponse extends CreateSampleResponse {
	private final long id;

	public CreateSampleSuccessResponse(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}

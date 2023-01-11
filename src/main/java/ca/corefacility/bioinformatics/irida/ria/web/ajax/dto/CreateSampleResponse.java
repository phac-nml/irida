package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * UI response to create a new sample
 */
public class CreateSampleResponse extends UpdateSampleResponse {
	private Long sampleId;

	public CreateSampleResponse(String errorMessage) {
		super(true, errorMessage);
	}

	public CreateSampleResponse(Long sampleId) {
		super(false);
		this.sampleId = sampleId;
	}

	public Long getSampleId() {
		return sampleId;
	}

	public void setSampleId(Long sampleId) {
		this.sampleId = sampleId;
	}
}

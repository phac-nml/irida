package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * UI response to create/update a sample with error and sample id
 */
public class SampleResponse extends SampleErrorResponse {
	private Long sampleId;

	public SampleResponse(String errorMessage) {
		super(true, errorMessage);
	}

	public SampleResponse(Long sampleId) {
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

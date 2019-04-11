package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

/**
 * Used as a request to remove a sample from the cart.
 */
public class RemoveSampleRequest {
	private Long projectId;
	private Long sampleId;

	public RemoveSampleRequest() {
	}

	public RemoveSampleRequest(Long projectId, Long sampleId) {
		this.projectId = projectId;
		this.sampleId = sampleId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public Long getSampleId() {
		return sampleId;
	}
}

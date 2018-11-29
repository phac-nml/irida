package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

public class CartIdResponse {
	private Long projectId;
	private Long sampleId;

	public CartIdResponse(Long projectId, Long sampleId) {
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

package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.Set;

public class CartRequest {
	private Long projectId;
	private Set<CartRequestSample> samples;

	public CartRequest() {
	}

	public CartRequest(Long projectId, Set<CartRequestSample> samples) {
		this.projectId = projectId;
		this.samples = samples;
	}

	public Long getProjectId() {
		return projectId;
	}

	public Set<CartRequestSample> getSamples() {
		return samples;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setSamples(Set<CartRequestSample> samples) {
		this.samples = samples;
	}
}

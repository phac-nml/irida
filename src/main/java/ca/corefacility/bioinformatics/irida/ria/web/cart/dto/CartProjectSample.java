package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Data transfer object for getting the samples that are in the cart and their relevant project identifier
 */

public class CartProjectSample {
	private Sample sample;
	private Long projectId;

	public CartProjectSample(Sample sample, Long projectId) {
		this.sample = sample;
		this.projectId = projectId;
	}

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProject(Long projectId) {
		this.projectId = projectId;
	}
}
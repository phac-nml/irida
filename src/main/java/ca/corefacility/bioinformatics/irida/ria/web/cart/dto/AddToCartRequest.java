package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Request sent from the UI to add {@link Sample}s from a {@link Project} to the cart.
 */
public class AddToCartRequest {
	/**
	 * {@link Project} identifier that the sample belong to.
	 */
	private Long projectId;

	/**
	 * List of {@link Sample} identifiers for samples to add to the cart.
	 */
	private List<Long> sampleIds;

	public AddToCartRequest() {
	}

	public AddToCartRequest(Long projectId, List<Long> samples) {
		this.projectId = projectId;
		this.sampleIds = samples;
	}

	public Long getProjectId() {
		return projectId;
	}

	public Iterable<Long> getSampleIds() {
		return sampleIds;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setSampleIds(List<Long> sampleIds) {
		this.sampleIds = sampleIds;
	}
}

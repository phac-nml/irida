package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Request sent from the UI to add {@link Sample} from a {@link Project} to the cart.
 */
public class AddToCartRequest {
	/**
	 * {@link Project} identifier that the sample belong to.
	 */
	private Long projectId;

	/**
	 * Set of {@link CartRequestSample} that mapping to a {@link Sample} to add to the cart.
	 */
	private Set<CartRequestSample> samples;

	public AddToCartRequest() {
	}

	public AddToCartRequest(Long projectId, Set<CartRequestSample> samples) {
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

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
	 * Set of {@link CartSampleRequest} that mapping to a {@link Sample} to add to the cart.
	 */
	private Set<CartSampleRequest> samples;

	public AddToCartRequest() {
	}

	public AddToCartRequest(Long projectId, Set<CartSampleRequest> samples) {
		this.projectId = projectId;
		this.samples = samples;
	}

	public Long getProjectId() {
		return projectId;
	}

	public Set<CartSampleRequest> getSamples() {
		return samples;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setSamples(Set<CartSampleRequest> samples) {
		this.samples = samples;
	}
}

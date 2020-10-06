package ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Session object to hold samples that are currently in the cart.
 */
public class Cart extends HashMap<Long, HashSet<Long>> {

	public int add(Long projectId, List<Long> sampleIds) {
		HashSet<Long> existing = this.containsKey(projectId) ? this.get(projectId) : new HashSet<>();
		existing.addAll(sampleIds);
		this.put(projectId, existing);
		return this.getNumberOfSamplesInCart();
	}

	/**
	 * Get the total number of samples in the cart
	 *
	 * @return Total samples from all project in the cart
	 */
	public int getNumberOfSamplesInCart() {
		return this.values()
				.stream()
				.reduce(0, (total, samples) -> total + samples.size(), Integer::sum);
	}

	/**
	 * Remove a specific sample from the cart.
	 *
	 * @param projectId Identifier of the project the sample is in
	 * @param sampleId  Identifier of the sample
	 * @return Total samples from all project in the cart
	 */
	public int removeSample(Long projectId, Long sampleId) {
		this.get(projectId)
				.remove(sampleId);
		return this.getNumberOfSamplesInCart();
	}

	/**
	 * Remove all samples from a specific project
	 *
	 * @param projectId Identifier of the project
	 * @return Total samples from all project in the cart
	 */
	public int removeProject(Long projectId) {
		this.remove(projectId);
		return getNumberOfSamplesInCart();
	}

	/**
	 * Get all the identifiers for projects in the cart
	 *
	 * @return Set of identifiers for projects in the cart
	 */
	public Set<Long> getProjectIdsInCart() {
		return this.keySet();
	}

	/**
	 * Get all the identifiers for samples belonging to a specific project in the cart.
	 *
	 * @param projectId Identifier of the project
	 * @return Set of identifiers for samples belonging to a project in the cart
	 */
	public Set<Long> getCartSampleIdsForProject(Long projectId) {
		return this.get(projectId);
	}
}

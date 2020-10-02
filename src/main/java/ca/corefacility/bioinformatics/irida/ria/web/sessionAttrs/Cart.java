package ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public int removeSample(Long projectId, Long sampleId) {
		this.get(projectId)
				.remove(sampleId);
		return this.getNumberOfSamplesInCart();
	}

	public int removeProject(Long projectId) {
		this.remove(projectId);
		return getNumberOfSamplesInCart();
	}

	public Set<Long> getProjectIdsInCart() {
		return this.keySet();
	}

	public Set<Long> getCartSampleIdsForProject(Long projectId) {
		return this.get(projectId);
	}
}

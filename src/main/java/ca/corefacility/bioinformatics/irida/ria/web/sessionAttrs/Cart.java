package ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.google.common.collect.Sets;

/**
 * Session object to hold samples that are currently in the cart.
 * HashMap<Sample ID, Project ID>
 */
public class Cart extends HashMap<Long, Long> {
	/*
	Sample Id --> Sample Name
	 */
	private final HashMap<Long, String> sampleNames = new HashMap<>();

	/**
	 * Add a sample to the cart
	 *
	 * @param sample    to add to the cart
	 * @param projectId identifier for the project from which the sample was added.
	 * @return the number of samples in the cart
	 */
	public int addSample(Sample sample, Long projectId) {
		this.put(sample.getId(), projectId);
		this.sampleNames.put(sample.getId(), sample.getLabel());
		return this.size();
	}

	/**
	 * Remove a sample from the cart.
	 *
	 * @param id for the sample to remove
	 * @return the number of samples in the cart
	 */
	public int removeSample(Long id) {
		this.remove(id);
		sampleNames.remove(id);
		return this.size();
	}

	/**
	 * Remove an entire project from the cart
	 *
	 * @param projectId identifier for the project to remove
	 * @return the number of samples in the cart
	 */
	public int removeProject(Long projectId) {
		Iterator<Entry<Long, Long>> iter = this.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<Long, Long> entry = iter.next();
			if (entry.getValue()
					.equals(projectId)) {
				iter.remove();
				sampleNames.remove(entry.getKey());
			}
		}

		this.forEach((key, value) -> {
			if (value.equals(projectId)) {
				this.remove(key);
				sampleNames.remove(key);
			}
		});
		return this.size();
	}

	/**
	 * Get the names of all the samples in the cart.
	 * This is needed because pipelines cannot have samples with the same name.
	 *
	 * @return List of names of samples in the cart
	 */
	public Set<String> getSampleNamesInCart() {
		return Sets.newHashSet(sampleNames.values());
	}

	/**
	 * Get all the identifiers for projects that have samples in the cart.
	 *
	 * @return List of project identifiers.
	 */
	public Set<Long> getProjectsIdsInCart() {
		return Sets.newHashSet(this.values());
	}

	/**
	 * Find if a sample is currently in the cart.  If it is get the project that it belongs to.
	 *
	 * @param sample - the sample to check the cart for.
	 * @return identifier for the project the sample is in if the sample is in the cart
	 */
	public Long isSampleInCart(Long sampleId) {
		return this.getOrDefault(sampleId, null);
	}
}

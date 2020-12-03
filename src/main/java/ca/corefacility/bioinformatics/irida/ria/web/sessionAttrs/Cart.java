package ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs;

import java.util.HashMap;
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

	public void addSample(Sample sample, Long projectId) {
		this.put(sample.getId(), projectId);
		this.sampleNames.put(sample.getId(), sample.getLabel());
	}

	public Set<String> getSampleNamesInCart() {
		return Sets.newHashSet( sampleNames.values());
	}

	public Set<Long> getProjectsIdsInCart() {
		return Sets.newHashSet(this.values());
	}
}

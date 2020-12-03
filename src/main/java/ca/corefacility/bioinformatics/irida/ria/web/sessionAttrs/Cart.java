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

	public int addSample(Sample sample, Long projectId) {
		this.put(sample.getId(), projectId);
		this.sampleNames.put(sample.getId(), sample.getLabel());
		return this.size();
	}

	public int removeSample(Long id) {
		this.remove(id);
		sampleNames.remove(id);
		return this.size();
	}

	public int removeProject(Long projectId) {
		Iterator<Entry<Long, Long>> iter = this.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<Long, Long> entry = iter.next();
			if (entry.getValue().equals(projectId)) {
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

	public Set<String> getSampleNamesInCart() {
		return Sets.newHashSet(sampleNames.values());
	}

	public Set<Long> getProjectsIdsInCart() {
		return Sets.newHashSet(this.values());
	}

}

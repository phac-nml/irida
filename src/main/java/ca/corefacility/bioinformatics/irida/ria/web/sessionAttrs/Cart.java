package ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

public class Cart extends HashMap<Project, HashSet<Sample>> {

	/**
	 * Add a list of {@link Sample}s from a specific {@link Project} to the cart
	 *
	 * @param project Project the sample belong to
	 * @param samples {@link List} of {@link Sample}s to add to the cart
	 * @return number of samples added to the car
	 */
	public int add(Project project, List<Sample> samples) {
		HashSet<Sample> existing = this.containsKey(project) ? this.get(project) : new HashSet<>();
		existing.addAll(samples);
		this.put(project, existing);
		return existing.size();
	}

	public int getNumberOfSamplesInCart() {
		return this.values().stream().reduce(0, (total, samples) -> total + samples.size(), Integer::sum);
	}
}

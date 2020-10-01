package ca.corefacility.bioinformatics.irida.ria.web.sessionAttrs;

import java.util.*;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

public class Cart extends HashMap<Project, HashSet<Sample>> {

	/**
	 * Add a list of {@link Sample}s from a specific {@link Project} to the cart
	 *
	 * @param project Project the sample belong to
	 * @param samples {@link List} of {@link Sample}s to add to the cart
	 * @return number of samples added to the cart
	 */
	public int add(Project project, List<Sample> samples) {
		HashSet<Sample> existing = this.containsKey(project) ? this.get(project) : new HashSet<>();
		existing.addAll(samples);
		this.put(project, existing);
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

	public int removeSample(Project project, Sample sample) {
		this.get(project)
				.remove(sample);
		return this.getNumberOfSamplesInCart();
	}

	public int removeProject(Project project) {
		this.remove(project);
		return getNumberOfSamplesInCart();
	}

	public List<Long> getProjectIdsInCart() {
		return this.keySet().stream().map(Project::getId).collect(Collectors.toUnmodifiableList());
	}

	public List<Sample> getCartSamplesForProject(List<Project> projects) {
		return projects.stream()
				.map(this::get)
				.collect(ArrayList::new, List::addAll, List::addAll);
	}
}

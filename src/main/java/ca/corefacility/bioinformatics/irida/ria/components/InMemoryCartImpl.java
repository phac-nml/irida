package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * A HashMap based {@link Cart}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Component
@Scope("session")
public class InMemoryCartImpl implements Cart {
	private Map<Project, Set<Sample>> selected = new HashMap<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeProject(Project project) {
		selected.remove(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addProjectSample(Project project, Set<Sample> samples) {
		Set<Sample> selectedSamplesForProject = getSelectedSamplesForProject(project);
		selectedSamplesForProject.addAll(samples);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeProjectSample(Project project, Set<Sample> samples) {
		Set<Sample> selectedSamplesForProject = getSelectedSamplesForProject(project);
		selectedSamplesForProject.removeAll(samples);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Sample> getSelectedSamplesForProject(Project project) {
		if (!selected.containsKey(project)) {
			selected.put(project, new HashSet<>());
		}

		return selected.get(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Cart [");
		for (Project p : selected.keySet()) {
			builder.append(p.getLabel() + " [");
			for (Sample s : selected.get(p)) {
				builder.append(" " + s.getLabel());
			}
			builder.append(" ] ");
		}
		builder.append(" ]");

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Project> getProjects() {
		return selected.keySet();
	}

}

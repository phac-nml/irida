package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

@Component
public class InMemoryCartImpl implements Cart {
	Map<Project, Set<Sample>> selected;

	@Override
	public void removeProject(Project project) {
		selected.remove(project);
	}

	@Override
	public void addProjectSample(Project project, Set<Sample> samples) {
		Set<Sample> selectedSamplesForProject = getSelectedSamplesForProject(project);
		selectedSamplesForProject.addAll(samples);
	}

	@Override
	public void removeProjectSample(Project project, Set<Sample> samples) {
		Set<Sample> selectedSamplesForProject = getSelectedSamplesForProject(project);
		selectedSamplesForProject.removeAll(samples);
	}

	@Override
	public Set<Sample> getSelectedSamplesForProject(Project project) {
		if (!selected.containsKey(project)) {
			selected.put(project, new HashSet<>());
		}

		return selected.get(project);
	}

}

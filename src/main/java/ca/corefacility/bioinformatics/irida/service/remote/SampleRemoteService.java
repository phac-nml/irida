package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Service for reading {@link Sample}s
 * 
 *
 */
public interface SampleRemoteService extends RemoteService<Sample> {
	/**
	 * Get the {@link Sample}s that exist in a {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} to get samples from
	 * @return A List of {@link Sample}s
	 */
	public List<Sample> getSamplesForProject(Project project);
}

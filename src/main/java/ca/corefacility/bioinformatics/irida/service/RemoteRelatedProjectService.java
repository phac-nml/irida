package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;

public interface RemoteRelatedProjectService extends CRUDService<Long, RemoteRelatedProject>{
	/**
	 * Get the {@link RemoteRelatedProject}s for a {@link Project}
	 * 
	 * @param project
	 *            The project to get related projects for
	 * @return A List of {@link RemoteRelatedProject}s
	 */
	public List<RemoteRelatedProject> getRemoteProjectsForProject(Project project);
}

package ca.corefacility.bioinformatics.irida.repositories.remote;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Service for reading Project objects from a remote IRIDA API
 */
public interface ProjectRemoteRepository extends RemoteRepository<Project> {
	/**
	 * Read the full project hash for the given project
	 *
	 * @param project the {@link Project} to get the hash for
	 * @return A deep hashcode for all objects in the project
	 */
	public Integer readProjectHash(Project project);

}

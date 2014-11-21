package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;

/**
 * Repository for storing/retrieving {@link RemoteRelatedProject} objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface RemoteRelatedProjectRepository extends IridaJpaRepository<RemoteRelatedProject, Long> {

	/**
	 * Get {@link RemoteRelatedProject}s for a given {@link Project}
	 * 
	 * @param project
	 *            The project to search for
	 * @return A list of {@link RemoteRelatedProject}s
	 */
	@Query("from RemoteRelatedProject p where p.localProject = ?1")
	public List<RemoteRelatedProject> getRemoteRelatedProjectsForProject(Project project);

	/**
	 * Get the {@link RemoteRelatedProject} object joining a given
	 * {@link Project} and remote uri
	 * 
	 * @param project
	 *            The local {@link Project}
	 * @param remoteProjectURI
	 *            The URI to the remote project
	 * @return A {@link RemoteRelatedProject} object for this relationship
	 */
	@Query("from RemoteRelatedProject p where p.localProject = ?1 AND p.remoteProjectURI = ?2")
	public RemoteRelatedProject getRemoteRelatedProjectForProjectAndURI(Project project, String remoteProjectURI);
}

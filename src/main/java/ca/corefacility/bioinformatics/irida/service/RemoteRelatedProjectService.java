package ca.corefacility.bioinformatics.irida.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;

/**
 * Service interface for managing {@link RemoteRelatedProject}s
 * 
 *
 */
public interface RemoteRelatedProjectService extends CRUDService<Long, RemoteRelatedProject> {
	/**
	 * Get the {@link RemoteRelatedProject}s for a {@link Project}
	 * 
	 * @param project
	 *            The project to get related projects for
	 * @return A List of {@link RemoteRelatedProject}s
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<RemoteRelatedProject> getRemoteProjectsForProject(Project project);

	/**
	 * Get the {@link RemoteRelatedProject} object joining a given
	 * {@link Project} and remote uri
	 * 
	 * @param project
	 *            The local {@link Project}
	 * @param remoteProjectURI
	 *            The URI to the remote project
	 * @return A {@link RemoteRelatedProject} object for this relationship
	 * @throws EntityNotFoundException
	 *             if the relationship doesn't exist
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public RemoteRelatedProject getRemoteRelatedProjectForProjectAndURI(Project project, String remoteProjectURI)
			throws EntityNotFoundException;
}

package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Service for reading Project objects from a remote IRIDA API
 * 
 *
 */
public interface ProjectRemoteService extends RemoteService<Project> {

	/**
	 * List all of the projects for a given {@link RemoteAPI}
	 * 
	 * @param api
	 *            The remote API to list projects for
	 * @return A List of {@link Project}s
	 */
	public List<Project> listProjectsForAPI(RemoteAPI api);

	public Integer getProjectHash(Project project);
}

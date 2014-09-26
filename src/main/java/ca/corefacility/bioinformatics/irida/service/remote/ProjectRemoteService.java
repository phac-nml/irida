package ca.corefacility.bioinformatics.irida.service.remote;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.service.remote.model.RemoteProject;

/**
 * Service for reading Project objects from a remote IRIDA API
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface ProjectRemoteService extends RemoteService<RemoteProject> {
	/**
	 * Read a {@link RemoteProject} for a given {@link RemoteRelatedProject}
	 * 
	 * @param project
	 *            The RemoteRelatedProject to read
	 * @return a {@link RemoteProject}
	 */
	public RemoteProject read(RemoteRelatedProject project);
}

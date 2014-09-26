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
	public RemoteProject read(RemoteRelatedProject project);
}

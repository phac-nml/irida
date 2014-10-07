package ca.corefacility.bioinformatics.irida.repositories.remote;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteIRIDARoot;

/**
 * {@link RemoteRepository} for reading {@link RemoteIRIDARoot}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface RemoteIRIDARootRepository extends RemoteRepository<RemoteIRIDARoot> {
	/**
	 * Get a {@link RemoteIRIDARoot} for a given {@link RemoteAPI}
	 * 
	 * @param api
	 *            The API to read the root for
	 * @return a {@link RemoteIRIDARoot}
	 */
	public RemoteIRIDARoot read(RemoteAPI api);
}

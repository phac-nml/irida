package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.RemoteResource;

/**
 * Repository for communicating with a remote IRIDA instance
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 * @param <Type> The type of object this repository will store
 */
public interface RemoteService<Type extends RemoteResource> {
	/**
	 * Read an individual resource
	 * @param id The ID of the resource to read
	 * @param remoteAPI the API to read from
	 * @return An object of Type
	 */
	public Type read(Long id, RemoteAPI remoteAPI);
	
	/**
	 * List the resources available from this service
	 * @param remoteAPI The API to read from
	 * @return A List<Type> of the resources available
	 */
	public List<Type> list(RemoteAPI remoteAPI);
	
}

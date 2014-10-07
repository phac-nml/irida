package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

public interface RemoteRepository<Type extends RemoteResource> {
	/**
	 * Read an individual resource
	 * 
	 * @param uri
	 *            The URI of the resource to read
	 * @param remoteAPI
	 *            the API to read from
	 * @return An object of Type
	 */
	public Type read(String uri, RemoteAPI remoteAPI);

	/**
	 * List the resources available from this service
	 * 
	 * @param remoteAPI
	 *            The API to read from
	 * @return A List<Type> of the resources available
	 */
	public List<Type> list(String uri, RemoteAPI remoteAPI);

	/**
	 * Get the status of the remote service
	 * 
	 * @param remoteAPI
	 *            The API to check status for
	 * @return true if the service is active
	 */
	public boolean getServiceStatus(RemoteAPI remoteAPI) throws IridaOAuthException;
}

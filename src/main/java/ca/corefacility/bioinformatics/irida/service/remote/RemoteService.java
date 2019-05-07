package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Service for communicating with a remote IRIDA instance
 * 
 *
 * @param <Type>
 *            The type of object this repository will store
 */
public interface RemoteService<Type extends IridaResourceSupport & IridaThing> {

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
	 * Read individual resource at the given URI. The required {@link RemoteAPI}
	 * will be found using the given resource URI.
	 * 
	 * @param uri
	 *            The URI of the resource
	 * @return an object of Type
	 */
	public Type read(String uri);

	/**
	 * List the resources available from this service
	 *
	 * @param uri
	 *            the uri to load the resources from.
	 * @param remoteAPI
	 *            The API to read from
	 * @return A {@code List<Type>} of the resources available
	 */
	public List<Type> list(String uri, RemoteAPI remoteAPI);

	/**
	 * Get the status of the remote service
	 *
	 * @param remoteAPI The API to check status for
	 * @return true if the service is active
	 * @throws IridaOAuthException if the remote api cannot be contacted
	 */
	public boolean getServiceStatus(RemoteAPI remoteAPI) throws IridaOAuthException;

}

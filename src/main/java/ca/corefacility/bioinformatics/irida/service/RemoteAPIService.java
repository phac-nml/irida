package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Service for storing and retrieving {@link RemoteAPI}s that this API can connect to
 *
 */
public interface RemoteAPIService extends CRUDService<Long, RemoteAPI> {

	/**
	 * Find the {@link RemoteAPI} where the serviceURI is a substring of the
	 * given URL.
	 * 
	 * For example if you passed the URL
	 * 'http://irida.ca/api/projects/5/samples/3' the method would find a
	 * {@link RemoteAPI} with serviceURI of 'http://irida.ca/api'
	 * 
	 * @param url
	 *            The URL to test
	 * @return The {@link RemoteAPI} matching the given URL
	 */
	public RemoteAPI getRemoteAPIForUrl(String url);

		
}

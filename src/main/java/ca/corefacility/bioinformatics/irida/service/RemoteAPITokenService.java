package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;

public interface RemoteAPITokenService {
	/**
	 * Add a token to the store for a given service
	 * 
	 * @param token
	 *            The token to create
	 */
	public void create(RemoteAPIToken token);
	
	/**
	 * Get a token for a given service
	 * 
	 * @param remoteAPI
	 *            The {@link RemoteAPI} of the service root
	 * @return A String OAuth2 token
	 * @throws EntityNotFoundException
	 *             if the token could not be found
	 */
	public RemoteAPIToken getToken(RemoteAPI remoteAPI) throws EntityNotFoundException;
	
	/**
	 * Delete a token for the logged in user and a given {@link RemoteAPI}
	 * 
	 * @param remoteAPI
	 *            the {@link RemoteAPI} to delete a token for
	 * @throws EntityNotFoundException
	 *             if the token could not be found
	 */
	public void delete(RemoteAPI remoteAPI) throws EntityNotFoundException;
}

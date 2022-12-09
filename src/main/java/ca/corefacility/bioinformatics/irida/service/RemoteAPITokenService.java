package ca.corefacility.bioinformatics.irida.service;

import java.net.URI;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;

/**
 * Service for saving and reading tokens for Remote APIs
 */
public interface RemoteAPITokenService {
	/**
	 * Add a token to the store for a given service
	 * 
	 * @param token The token to create
	 * @return the created token
	 */
	public RemoteAPIToken create(RemoteAPIToken token);

	/**
	 * Get a token for a given service
	 * 
	 * @param remoteAPI The {@link RemoteAPI} of the service root
	 * @return A String OAuth2 token
	 * @throws EntityNotFoundException if the token could not be found
	 */
	public RemoteAPIToken getToken(RemoteAPI remoteAPI) throws EntityNotFoundException;

	/**
	 * Delete a token for the logged in user and a given {@link RemoteAPI}
	 * 
	 * @param remoteAPI the {@link RemoteAPI} to delete a token for
	 * @throws EntityNotFoundException if the token could not be found
	 */
	public void delete(RemoteAPI remoteAPI) throws EntityNotFoundException;

	/**
	 * Create a new {@link RemoteAPIToken} from a given auth code
	 *
	 * @param authcode      the auth code to create a token for
	 * @param remoteAPI     the remote api to get a token for
	 * @param tokenRedirect a redirect url to get the token from
	 * @return the newly created token
	 * @throws ParseException If there's a problem with the token request
	 */
	public RemoteAPIToken createTokenFromAuthCode(AuthorizationCode authcode, RemoteAPI remoteAPI, URI tokenRedirect)
			throws ParseException;

	/**
	 * Update a given {@link RemoteAPI}'s OAuth token by refresh token if available
	 * 
	 * @param api the API to update
	 * @return the most recent token if available
	 */
	public RemoteAPIToken updateTokenFromRefreshToken(RemoteAPI api);
}

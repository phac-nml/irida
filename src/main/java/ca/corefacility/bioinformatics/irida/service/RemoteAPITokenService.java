package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;

public interface RemoteAPITokenService {
	/**
	 * Add a token to the store for a given service
	 * @param serviceURI The URI of the service root
	 * @param token The token string
	 */
	public void addToken(RemoteAPIToken token);
	
	/**
	 * Get a token for a given service 
	 * @param serviceURI The URI of the service root
	 * @return A String OAuth2 token
	 */
	public RemoteAPIToken getToken(RemoteAPI remoteAPI);
}

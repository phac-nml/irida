package ca.corefacility.bioinformatics.irida.repositories.remote.token;

import java.net.URI;

import org.springframework.stereotype.Component;

/**
 * OAuth token repository for storing and retrieving tokens for OAuth2 protected services
 * @author tom
 *
 */
@Component
public interface TokenRepository {

	/**
	 * Add a token to the store for a given service
	 * @param serviceURI The URI of the service root
	 * @param token The token string
	 */
	public void addToken(URI serviceURI, String token);
	
	/**
	 * Get a token for a given service 
	 * @param serviceURI The URI of the service root
	 * @return A String OAuth2 token
	 */
	public String getToken(URI serviceURI);

}
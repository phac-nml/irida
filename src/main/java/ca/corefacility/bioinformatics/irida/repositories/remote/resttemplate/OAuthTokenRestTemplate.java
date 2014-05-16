package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Rest Template used to communicate with OAuth2 enabled REST APIs. Uses a
 * {@link RemoteAPITokenService} to read OAuth2 tokens to use.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class OAuthTokenRestTemplate extends RestTemplate{
	private RemoteAPITokenService tokenService;
	
	private RemoteAPI remoteAPI;
	private IridaOAuthErrorHandler errorHandler = new IridaOAuthErrorHandler();

	public OAuthTokenRestTemplate(RemoteAPITokenService tokenService, RemoteAPI remoteAPI) {
		super();
		this.tokenService = tokenService;
		this.setRemoteAPI(remoteAPI);
		this.setErrorHandler(errorHandler);
	}

	/**
	 * Create a new OAuthTokenRestTemplate with the given {@link RemoteAPITokenService} and {@link ClientHttpRequestFactory}
	 * @param tokenService
	 * @param requestFactory
	 */
	public OAuthTokenRestTemplate(RemoteAPITokenService tokenService,ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
		this.tokenService = tokenService;
		this.setErrorHandler(errorHandler);
	}
	
	/**
	 * Add an OAuth token from the tokenRepository to the request
	 */
	@Override
	protected ClientHttpRequest createRequest(URI uri, HttpMethod method) throws IOException {
		RemoteAPIToken token;
		
		try{
			token = tokenService.getToken(remoteAPI);
		}
		catch(EntityNotFoundException ex){
			logger.debug("No token found for service " + remoteAPI);
			throw new IridaOAuthException("No token fround for service",remoteAPI,ex);
		}
		
		if(token.isExpired()){
			logger.debug("Token for service is expired " + remoteAPI);
			throw new IridaOAuthException("Token is expired for service",remoteAPI);
		}
		
		ClientHttpRequest createRequest = super.createRequest(uri, method);
		createRequest.getHeaders().add("Authorization", "Bearer " + token.getTokenString());
		
		return createRequest;
	}
	
	@Override
	public ResponseErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	/**
	 * Set the API this rest template will communicate with
	 * @param remoteAPI
	 */
	public void setRemoteAPI(RemoteAPI remoteAPI){
		this.remoteAPI = remoteAPI;
		errorHandler.setRemoteAPI(remoteAPI);
	}
}

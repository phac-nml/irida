package ca.corefacility.bioinformatics.irida.repositories.remote.oltu;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.repositories.remote.token.TokenRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

public class OAuthTokenRestTemplate extends RestTemplate{
	private RemoteAPITokenService tokenService;
	
	private RemoteAPI remoteAPI;
	private IridaOAuthErrorHandler errorHandler = new IridaOAuthErrorHandler();

	public OAuthTokenRestTemplate(RemoteAPITokenService tokenService) {
		super();
		this.tokenService = tokenService;
		this.setErrorHandler(errorHandler);
	}

	/**
	 * Create a new OAuthTokenRestTemplate with the given {@link TokenRepository} and {@link ClientHttpRequestFactory}
	 * @param tokenRepository
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
		RemoteAPIToken token = tokenService.getToken(remoteAPI);
		
		if (token == null) {
			logger.debug("No token found for service " + remoteAPI);
			throw new IridaOAuthException("No token fround for service",remoteAPI);
		}
		
		ClientHttpRequest createRequest = super.createRequest(uri, method);
		createRequest.getHeaders().add("Authorization", "Bearer " + token.getTokenString());
		
		return createRequest;
	}
	
	@Override
	public ResponseErrorHandler getErrorHandler() {
		return errorHandler;
	}
	
	public void setRemoteAPI(RemoteAPI remoteAPI){
		this.remoteAPI = remoteAPI;
		errorHandler.setRemoteAPI(remoteAPI);
	}
}

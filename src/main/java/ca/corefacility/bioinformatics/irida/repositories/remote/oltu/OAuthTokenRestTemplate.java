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
import ca.corefacility.bioinformatics.irida.repositories.remote.token.TokenRepository;

public class OAuthTokenRestTemplate extends RestTemplate{
	private TokenRepository tokenRepository;
	
	private RemoteAPI remoteAPI;
	private IridaOAuthErrorHandler errorHandler = new IridaOAuthErrorHandler();

	public OAuthTokenRestTemplate(TokenRepository tokenRepository) {
		super();
		this.tokenRepository = tokenRepository;
	}

	/**
	 * Create a new OAuthTokenRestTemplate with the given {@link TokenRepository} and {@link ClientHttpRequestFactory}
	 * @param tokenRepository
	 * @param requestFactory
	 */
	public OAuthTokenRestTemplate(TokenRepository tokenRepository,	ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
		this.tokenRepository = tokenRepository;
		this.setErrorHandler(errorHandler);
	}
	
	/**
	 * Add an OAuth token from the tokenRepository to the request
	 */
	@Override
	protected ClientHttpRequest createRequest(URI uri, HttpMethod method) throws IOException {
		String token = tokenRepository.getToken(remoteAPI);
		
		if (token == null) {
			logger.debug("No token found for service " + remoteAPI);
			throw new IridaOAuthException("No token fround for service",remoteAPI);
		}
		
		ClientHttpRequest createRequest = super.createRequest(uri, method);
		createRequest.getHeaders().add("Authorization", "Bearer " + token);
		
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

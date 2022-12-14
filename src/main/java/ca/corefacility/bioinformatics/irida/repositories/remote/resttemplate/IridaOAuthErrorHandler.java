package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Error handler for use in {@link OAuthTokenRestTemplate}. Catches HTTP UNAUTHORIZED (401) and FORBIDDEN (403) errors
 * to translate to IridaOAuthExceptions
 */
public class IridaOAuthErrorHandler extends DefaultResponseErrorHandler {
	private static final Logger logger = LoggerFactory.getLogger(IridaOAuthErrorHandler.class);

	private RemoteAPI remoteAPI;

	/**
	 * Overriding this method to throw a {@link IridaOAuthException} in case of an HTTP UNAUTHORIZED response.
	 */
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {

		HttpStatus statusCode = response.getStatusCode();
		logger.trace("Checking error type " + statusCode.toString());
		switch (statusCode) {
		case UNAUTHORIZED:
			logger.trace("Throwing new IridaOAuthException (Unauthorized) for this error");
			throw new IridaOAuthException("User is unauthorized for this service", statusCode, remoteAPI);
		case FORBIDDEN:
			logger.trace("Throwing new IridaOAuthException (Forbidden) for this error");
			throw new IridaOAuthException("User is forbidden from accessing this resource", statusCode, remoteAPI);
		case NOT_FOUND:
			logger.trace("Throwing new IridaOAuthException for this error");
			throw new IridaOAuthException("Resource not found", statusCode, remoteAPI);
		default:
			logger.trace("Passing error to superclass");
			super.handleError(response);
		}
	}

	/**
	 * Set the {@link RemoteAPI} to return to the caller if an error occurs
	 * 
	 * @param remoteAPI the {@link RemoteAPI} to include in the exception.
	 */
	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}
}

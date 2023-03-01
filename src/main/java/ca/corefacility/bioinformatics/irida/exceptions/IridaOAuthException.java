package ca.corefacility.bioinformatics.irida.exceptions;

import org.springframework.http.HttpStatus;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;

/**
 * Exception thrown when attempting to read from a remote API via OAuth2 fails.
 * This exception will often be thrown when a {@link RemoteAPIToken} is invalid
 * or expired.
 * 
 *
 */
public class IridaOAuthException extends RuntimeException {

	private static final long serialVersionUID = 5281201199554307578L;
	private RemoteAPI remoteAPI;
	private HttpStatus httpStatusCode;

	/**
	 * Create a new IridaOAuthException with the given message and service
	 * 
	 * @param message
	 *            The message for this exception
	 * @param remoteAPI
	 *            The service trying to be accessed when this exception was
	 *            thrown
	 */
	public IridaOAuthException(String message, RemoteAPI remoteAPI) {
		super(message);
		this.remoteAPI = remoteAPI;
	}

	public IridaOAuthException(String message, HttpStatus httpStatusCode, RemoteAPI remoteAPI) {
		super(message);
		this.httpStatusCode = httpStatusCode;
		this.remoteAPI = remoteAPI;

	}

	/**
	 * Create a new IridaOAuthException with the given message and service
	 * 
	 * @param message
	 *            The message for this exception
	 * @param remoteAPI
	 *            The service trying to be accessed when this exception was
	 *            thrown
	 * @param cause
	 *            The reason this exception was thrown
	 */
	public IridaOAuthException(String message, RemoteAPI remoteAPI, Throwable cause) {
		super(message, cause);
		this.remoteAPI = remoteAPI;
	}

	/**
	 * Get the service trying to be accessed when this exception was thrown
	 * 
	 * @return The URI of the service
	 */
	public RemoteAPI getRemoteAPI() {
		return remoteAPI;
	}

	/**
	 * Set the service for this exception
	 * 
	 * @param remoteAPI
	 *            the URI of the service
	 */
	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}

	public HttpStatus getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(HttpStatus httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
}

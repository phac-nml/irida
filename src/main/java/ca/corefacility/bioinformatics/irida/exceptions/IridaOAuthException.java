package ca.corefacility.bioinformatics.irida.exceptions;

import java.net.URI;

public class IridaOAuthException extends RuntimeException{
	private static final long serialVersionUID = -4320386609045897703L;
	
	private URI service;
	
	/**
	 * Create a new IridaOAuthException with the given message and service
	 * @param message The message for this exception
	 * @param service The service trying to be accessed when this exception was thrown
	 */
	public IridaOAuthException(String message, URI service){
		super(message);
		this.setService(service);
	}

	/**
	 * Get the service trying to be accessed when this exception was thrown
	 * @return The URI of the service
	 */
	public URI getService() {
		return service;
	}

	/**
	 * Set the service for this exception
	 * @param service the URI of the service
	 */
	public void setService(URI service) {
		this.service = service;
	}
}

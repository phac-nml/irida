package ca.corefacility.bioinformatics.irida.repositories.remote.oltu;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;

public class IridaOAuthErrorHandler extends DefaultResponseErrorHandler{
	private static final Logger logger = LoggerFactory.getLogger(IridaOAuthErrorHandler.class);
	
	private URI service;

	public void handleError(ClientHttpResponse response) throws IOException{
		
		HttpStatus statusCode = response.getStatusCode();
		logger.trace("Checking error type " + statusCode.toString());
		switch(statusCode){
			case UNAUTHORIZED:
				logger.trace("Throwing new IridaOAuthException for this error");
				throw new IridaOAuthException("User is unauthorized for this service", service);
			default:
				logger.trace("Passing error to superclass");
				super.handleError(response);
		}
	}
	
	public void setService(URI service){
		this.service = service;
	}
}

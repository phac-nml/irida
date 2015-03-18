package ca.corefacility.bioinformatics.irida.web.controller.api.exception;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

/**
 * Error message response class to be serialzed to Json and returned from the
 * {@link ControllerExceptionHandler}
 * 
 * 
 */
public class ErrorResponse {

	private Map<String, Object> otherProperties;
	private String message;

	public ErrorResponse() {
	}

	public ErrorResponse(String message) {
		this.message = message;
		otherProperties = new HashMap<>();
	}

	public ErrorResponse(String message, Map<String, Object> otherProperties) {
		this.message = message;
		this.otherProperties = otherProperties;
	}

	/**
	 * Get the message for this response
	 * 
	 * @return the message for this response
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message for this response
	 * 
	 * @param message
	 *            the message for this response
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Add an additional map property to be serialized to JSON
	 * 
	 * @param key
	 *            the key of the additional property
	 * @param value
	 *            the value of the additional property
	 */
	public void addProperty(String key, Object value) {
		otherProperties.put(key, value);
	}

	/**
	 * Get the other properties defined in the map
	 * 
	 * @return the other properties defined in the map
	 */
	@JsonAnyGetter
	public Map<String, Object> getOtherProperties() {
		return otherProperties;
	}

	/**
	 * Set the additional properties to display for this response
	 * 
	 * @param otherProperties the additional properties to display for this message.
	 */
	public void setOtherProperties(Map<String, Object> otherProperties) {
		this.otherProperties = otherProperties;
	}
}

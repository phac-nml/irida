package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception thrown when there is no link for a given rel when reading from an API
 */
public class LinkNotFoundException extends RuntimeException {

	public LinkNotFoundException(String message) {
		super(message);
	}
}

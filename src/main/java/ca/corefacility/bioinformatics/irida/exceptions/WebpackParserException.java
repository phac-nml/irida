package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown web there is an error parsing the webpack assets-manifest file.
 */
public class WebpackParserException extends RuntimeException {
	public WebpackParserException(String message) {
		super(message);

	}
}

package ca.corefacility.bioinformatics.irida.exceptions;

import java.io.File;

/**
 * Thrown web there is an error parsing the webpack assets-manifest file.
 */
public class WebpackParserException extends RuntimeException {
	public WebpackParserException(File file, String message) {
		super("Error parsing webpack asseets-manifist files [" + file.getAbsolutePath() + "]: " + message);

	}
}

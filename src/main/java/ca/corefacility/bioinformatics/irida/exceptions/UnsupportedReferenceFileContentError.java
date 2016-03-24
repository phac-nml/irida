package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Error that's thrown when BioJava can't parse an uploaded reference file.
 *
 */
public class UnsupportedReferenceFileContentError extends Error {

	public UnsupportedReferenceFileContentError(final String message, final Error cause) {
		super(message, cause);
	}
}

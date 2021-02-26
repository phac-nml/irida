package ca.corefacility.bioinformatics.irida.exceptions.pipelines;

/**
 * This exception is thrown if a pipeline is attempted to be launched that
 * requires a reference file, but does not have one.
 */
public class ReferenceFileRequiredException extends Exception {

	public ReferenceFileRequiredException(final String message) {
		super(message);
	}
}

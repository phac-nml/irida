package ca.corefacility.bioinformatics.irida.exceptions.pipelines;

/**
 * This exception is thrown if a pipeline is attempted to be launched that
 * has required parameters that haven't been provided
 */
public class MissingRequiredParametersException extends Exception {
	public MissingRequiredParametersException(final String message) {
		super(message);
	}
}

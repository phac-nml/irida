package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown when there is an issue with a SequenceFile analysis.
 *
 */
public class SequenceFileAnalysisException extends Exception {

	private static final long serialVersionUID = -8338868725003259599L;

	/**
	 * Construct a new {@link SequenceFileAnalysisException} with the specified message.
	 *
	 * @param message
	 *            the message explaining the exception.
	 */
	public SequenceFileAnalysisException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@link SequenceFileAnalysisException} with the specified
	 * message and original cause.
	 * 
	 * @param message
	 *            the message explaining the exception.
	 * @param cause
	 *            the original cause of the exception
	 */
	public SequenceFileAnalysisException(String message, Throwable cause) {
		super(message, cause);
	}
}

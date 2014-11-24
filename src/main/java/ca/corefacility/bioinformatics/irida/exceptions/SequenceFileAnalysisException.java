package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown when there is an issue with a SequenceFile analysis.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
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
	 * Construct a new {@link SequenceFileAnalysisException} with the specified message and
	 * original cause.
	 * 
	 * @param message
	 * @param cause
	 */
	public SequenceFileAnalysisException(String message, Throwable cause) {
		super(message, cause);
	}
}

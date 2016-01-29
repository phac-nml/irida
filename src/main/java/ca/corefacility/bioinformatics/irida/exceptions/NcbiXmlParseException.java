package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception thrown when there's an error parsing NCBI responses
 */
public class NcbiXmlParseException extends Exception {

	/**
	 * Create NcbiXmlParseException with message and cause
	 * 
	 * @param message
	 *            reason the exception is thrown
	 * @param cause
	 *            throwable cause
	 */
	public NcbiXmlParseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create NcbiXmlParseException with message
	 * 
	 * @param message
	 *            Reason NcbiXmlParseException is thrown
	 */
	public NcbiXmlParseException(String message) {
		super(message);
	}

}

package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.processing.concatenate.SequencingObjectConcatenator;

/**
 * Exception thrown when there's an issue with a {@link SequencingObjectConcatenator}
 */
public class ConcatenateException extends Exception {
	public ConcatenateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConcatenateException(String message) {
		super(message);
	}
}

package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception thrown when a remote file is attempted to be used before it has
 * been cached locally.
 */
public class RemoteFileNotCachedException extends RuntimeException {
	public RemoteFileNotCachedException(String message) {
		super(message);
	}

	public RemoteFileNotCachedException(String message, Throwable cause) {
		super(message, cause);
	}
}

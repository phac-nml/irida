package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

/**
 * When there is an issue changing library permissions.
 *
 */
public class ChangeLibraryPermissionsException extends UploadException {
	private static final long serialVersionUID = 4857770214742199369L;

	/**
	 * Constructs a new ChangeLibraryPermissionsException with no information.
	 */
	public ChangeLibraryPermissionsException() {
		super();
	}

	/**
	 * Constructs a new ChangeLibraryPermissionsException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public ChangeLibraryPermissionsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new ChangeLibraryPermissionsException with the given message.
	 * @param message  The message explaining the error.
	 */
	public ChangeLibraryPermissionsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new ChangeLibraryPermissionsException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public ChangeLibraryPermissionsException(Throwable cause) {
		super(cause);
	}
}

package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

/**
 * When there is an issue connecting to Galaxy.
 *
 */
public class GalaxyConnectException extends UploadException {
	private static final long serialVersionUID = 2395605818272983294L;

	/**
	 * Constructs a new GalaxyConnectException with no information.
	 */
	public GalaxyConnectException() {
		super();
	}

	/**
	 * Constructs a new GalaxyConnectException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyConnectException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new GalaxyConnectException with the given message.
	 * @param message  The message explaining the error.
	 */
	public GalaxyConnectException(String message) {
		super(message);
	}

	/**
	 * Constructs a new GalaxyConnectException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public GalaxyConnectException(Throwable cause) {
		super(cause);
	}
}

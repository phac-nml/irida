package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerDownloadException;

/**
 * Thrown when there is an issue downloading data from Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyDownloadException extends ExecutionManagerDownloadException {

	private static final long serialVersionUID = 8584626377900537775L;

	/**
	 * Constructs a new GalaxyDownloadException with no information.
	 */
	public GalaxyDownloadException() {
		super();
	}

	/**
	 * Constructs a new GalaxyDownloadException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyDownloadException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new GalaxyDownloadException with the given message.
	 * @param message  The message explaining the error.
	 */
	public GalaxyDownloadException(String message) {
		super(message);
	}

	/**
	 * Constructs a new GalaxyDownloadException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public GalaxyDownloadException(Throwable cause) {
		super(cause);
	}
}

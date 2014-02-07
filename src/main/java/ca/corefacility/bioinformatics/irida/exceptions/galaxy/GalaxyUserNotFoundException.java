package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;

/**
 * When no user is found in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUserNotFoundException extends UploadException {
	private static final long serialVersionUID = 2496168579584339258L;

	/**
	 * Constructs a new GalaxyUserNotFoundException with no information.
	 */
	public GalaxyUserNotFoundException() {
		super();
	}
	
	/**
	 * Constructs a new GalaxyUserNotFoundException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyUserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new GalaxyUserNotFoundException with the given message.
	 * @param message  The message explaining the error.
	 */
	public GalaxyUserNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new GalaxyUserNotFoundException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public GalaxyUserNotFoundException(Throwable cause) {
		super(cause);
	}
}

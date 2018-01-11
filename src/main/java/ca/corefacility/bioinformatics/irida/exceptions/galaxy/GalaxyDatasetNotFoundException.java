package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

/**
 * Exception thrown if a given dataset is not found in galaxy
 */
public class GalaxyDatasetNotFoundException extends GalaxyDatasetException {

	private static final long serialVersionUID = -9021987573289725500L;

	/**
	 * Constructs a new GalaxyDatasetNotFoundException with no information.
	 */
	public GalaxyDatasetNotFoundException() {
		super();
	}

	/**
	 * Constructs a new GalaxyDatasetNotFoundException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyDatasetNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new GalaxyDatasetNotFoundException with the given message.
	 * @param message  The message explaining the error.
	 */
	public GalaxyDatasetNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs a new GalaxyDatasetNotFoundException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public GalaxyDatasetNotFoundException(Throwable cause) {
		super(cause);
	}
}

package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;

/**
 * When there is no role for a user within Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUserNoRoleException extends ExecutionManagerObjectNotFoundException {
	private static final long serialVersionUID = 7783940011662578668L;

	/**
	 * Constructs a new GalaxyUserNoRoleException with no information.
	 */
	public GalaxyUserNoRoleException() {
		super();
	}

	/**
	 * Constructs a new GalaxyUserNoRoleException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyUserNoRoleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new GalaxyUserNoRoleException with the given message.
	 * @param message  The message explaining the error.
	 */
	public GalaxyUserNoRoleException(String message) {
		super(message);
	}

	/**
	 * Constructs a new GalaxyUserNoRoleException with the given cause.
	 * @param cause  The cause of this error.
	 */
	public GalaxyUserNoRoleException(Throwable cause) {
		super(cause);
	}
}

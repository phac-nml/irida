package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown when a user attempts to update a password with one they've already used.
 */
public class PasswordReusedException extends RuntimeException {

	/**
	 * Create a new {@link PasswordReusedException} with the given message
	 *
	 * @param message message to report
	 */
	public PasswordReusedException(String message) {
		super(message);
	}
}

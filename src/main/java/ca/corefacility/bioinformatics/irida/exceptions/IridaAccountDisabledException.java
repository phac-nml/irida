package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown when a password reset is requested by a user whose account is disabled
 *
 */

public class IridaAccountDisabledException extends Exception {
	public IridaAccountDisabledException(final String message) {
		super(message);
	}
}

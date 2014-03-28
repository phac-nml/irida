package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

/**
 * When no user is found in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUserNotFoundException extends UploadException {
	private static final long serialVersionUID = 2496168579584339258L;
	
	private GalaxyAccountEmail userEmail;

	/**
	 * Constructs a new GalaxyUserNotFoundException with the given user email.
	 * @param userEmail The user email of the user not found.
	 */
	public GalaxyUserNotFoundException(GalaxyAccountEmail userEmail) {
		super("Could not find Galaxy user " + userEmail);
		this.userEmail = userEmail;
	}
	
	/**
	 * Constructs a new GalaxyUserNotFoundException with the given user email, message and cause.
	 * @param userEmail The user email of the user not found.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public GalaxyUserNotFoundException(GalaxyAccountEmail userEmail, String message, Throwable cause) {
		super(message + " for user " + userEmail, cause);
		this.userEmail = userEmail;
	}

	/**
	 * Constructs a new GalaxyUserNotFoundException with the given user email and message.
	 * @param userEmail The user email of the user not found.
	 * @param message  The message explaining the error.
	 */
	public GalaxyUserNotFoundException(GalaxyAccountEmail userEmail, String message) {
		super(message + " for user " + userEmail);
		this.userEmail = userEmail;
	}

	/**
	 * Constructs a new GalaxyUserNotFoundException with the given user email and cause.
	 * @param userEmail The user email of the user not found.
	 * @param cause  The cause of this error.
	 */
	public GalaxyUserNotFoundException(GalaxyAccountEmail userEmail, Throwable cause) {
		super("Could not find Galaxy user " + userEmail, cause);
		this.userEmail = userEmail;
	}
	
	/**
	 * Gets the email address of the user not found.
	 * @return  The email address of the user not found.
	 */
	public GalaxyAccountEmail getUserEmail() {
		return userEmail;
	}
}

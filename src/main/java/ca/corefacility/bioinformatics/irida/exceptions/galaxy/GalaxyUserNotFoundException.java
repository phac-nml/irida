package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

import java.net.URL;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

/**
 * When no user is found in Galaxy.
 *
 */
public class GalaxyUserNotFoundException extends ExecutionManagerObjectNotFoundException {
	private static final long serialVersionUID = 2496168579584339258L;
	
	private GalaxyAccountEmail userEmail;
	private URL galaxyURL;

	/**
	 * Constructs a new GalaxyUserNotFoundException with the given user email.
	 * @param userEmail The user email of the user not found.
	 * @param galaxyURL The URL to galaxy where the error occured.
	 */
	public GalaxyUserNotFoundException(GalaxyAccountEmail userEmail, URL galaxyURL) {
		super("Could not find Galaxy user " + userEmail + " in Galaxy " + galaxyURL);
		this.userEmail = userEmail;
		this.galaxyURL = galaxyURL;
	}

	/**
	 * Constructs a new GalaxyUserNotFoundException with the given user email and cause.
	 * @param userEmail The user email of the user not found.
	 * @param galaxyURL The URL to galaxy where the error occured.
	 * @param cause  The cause of this error.
	 */
	public GalaxyUserNotFoundException(GalaxyAccountEmail userEmail, URL galaxyURL, Throwable cause) {
		super("Could not find Galaxy user " + userEmail + " in Galaxy " + galaxyURL, cause);
		this.userEmail = userEmail;
		this.galaxyURL = galaxyURL;
	}
	
	/**
	 * Gets the email address of the user not found.
	 * @return  The email address of the user not found.
	 */
	public GalaxyAccountEmail getUserEmail() {
		return userEmail;
	}
	
	/**
	 * Gets the URL of Galaxy where the user was not found.
	 * @return  The URL of Galaxy where the user was not found.
	 */
	public URL getGalaxyURL() {
		return galaxyURL;
	}
}

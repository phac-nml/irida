package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.Optional;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.User;

/**
 * Class for defining methods used to search for different Galaxy users.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUserSearch {

	private URL galaxyURL;
	private UsersClient usersClient;
	
	/**
	 * Builds a new GalaxyUserSearch for searching for Galaxy users.
	 * @param usersClient  The UsersClient connected to a Galaxy instance.
	 * @param galaxyURL  The URL of the Galaxy instance.
	 */
	public GalaxyUserSearch(UsersClient usersClient, URL galaxyURL) {
		checkNotNull(usersClient, "usersClient is null");
		checkNotNull(galaxyURL, "galaxyURL is null");
		
		this.usersClient = usersClient;
		this.galaxyURL = galaxyURL;
	}
	
	/**
	 * Given an email, finds a corresponding User object in Galaxy with that
	 * email.
	 * 
	 * @param email
	 *            The email of the user to search.
	 * @return A User object of the user with the corresponding email.
	 * @throws GalaxyUserNotFoundException
	 *             If the user could not be found.
	 */
	public User findUserWithEmail(GalaxyAccountEmail email) throws GalaxyUserNotFoundException {
		checkNotNull(email, "email is null");

		if (usersClient != null) {
			Optional<User> u = usersClient.getUsers().stream()
					.filter((user) -> user.getEmail().equals(email.getName())).findFirst();
			if (u.isPresent()) {
				return u.get();
			}
		}

		throw new GalaxyUserNotFoundException(email, galaxyURL);
	}

	/**
	 * Determines if the passed Galaxy user exists within the Galaxy instance.
	 * 
	 * @param galaxyUserEmail
	 *            The user email address to check.
	 * @return True if this user exists, false otherwise.
	 */
	public boolean galaxyUserExists(GalaxyAccountEmail galaxyUserEmail) {
		try {
			return findUserWithEmail(galaxyUserEmail) != null;
		} catch (GalaxyUserNotFoundException e) {
			return false;
		}
	}
}

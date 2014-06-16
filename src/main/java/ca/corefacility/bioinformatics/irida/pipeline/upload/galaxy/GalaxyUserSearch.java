package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.Optional;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.User;

/**
 * Class for defining methods used to search for different Galaxy users.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUserSearch extends GalaxySearch<User, GalaxyAccountEmail> {

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
	 * {@inheritDoc}
	 */
	@Override
	public User findById(GalaxyAccountEmail id) throws ExecutionManagerObjectNotFoundException {
		checkNotNull(id, "id is null");

		if (usersClient != null) {
			Optional<User> u = usersClient.getUsers().stream()
					.filter((user) -> user.getEmail().equals(id.getName())).findFirst();
			if (u.isPresent()) {
				return u.get();
			}
		}

		throw new GalaxyUserNotFoundException(id, galaxyURL);
	}
}

package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.Optional;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Role;

/**
 * Class defining methods for searching for different Galaxy roles.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyRoleSearch {

	private RolesClient rolesClient;
	private URL galaxyURL;
	
	public GalaxyRoleSearch(RolesClient rolesClient, URL galaxyURL) {
		checkNotNull(rolesClient, "rolesClient is null");
		checkNotNull(galaxyURL, "galaxyURL is null");
		
		this.rolesClient = rolesClient;
		this.galaxyURL = galaxyURL;
	}
	
	/**
	 * Given an email, finds a corresponding users private Role object in Galaxy
	 * with that email.
	 * 
	 * @param email
	 *            The email of the user to search.
	 * @return A private Role object of the user with the corresponding email.
	 * @throws GalaxyUserNoRoleException
	 *             If no role for the user could be found.
	 */
	public Role findUserRoleWithEmail(GalaxyAccountEmail email) throws GalaxyUserNoRoleException {
		checkNotNull(email, "email is null");

		Optional<Role> r = rolesClient.getRoles().stream().filter((role) -> role.getName().equals(email.getName()))
				.findFirst();
		if (r.isPresent()) {
			return r.get();
		}

		throw new GalaxyUserNoRoleException("No role found for " + email + " in Galaxy "
				+ galaxyURL);
	}
	

	/**
	 * Determines if a role exists for the given user.
	 * 
	 * @param galaxyUserEmail
	 *            The user to search for a role.
	 * @return True if a role exists for the user, false otherwise.
	 */
	public boolean userRoleExistsFor(GalaxyAccountEmail galaxyUserEmail) {
		try {
			Role role = findUserRoleWithEmail(galaxyUserEmail);
			return role != null;
		} catch (GalaxyUserNoRoleException e) {
			return false;
		}
	}
}

package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.Optional;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Role;

/**
 * Class defining methods for searching for different Galaxy roles.
 *
 */
public class GalaxyRoleSearch extends GalaxySearch<Role, GalaxyAccountEmail> {

	private RolesClient rolesClient;
	private URL galaxyURL;
	
	public GalaxyRoleSearch(RolesClient rolesClient, URL galaxyURL) {
		checkNotNull(rolesClient, "rolesClient is null");
		checkNotNull(galaxyURL, "galaxyURL is null");
		
		this.rolesClient = rolesClient;
		this.galaxyURL = galaxyURL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role findById(GalaxyAccountEmail id)
			throws ExecutionManagerObjectNotFoundException {
		checkNotNull(id, "id is null");

		Optional<Role> r = rolesClient.getRoles().stream()
				.filter((role) -> role.getName().equals(id.getName()))
				.findFirst();
		if (r.isPresent()) {
			return r.get();
		}

		throw new GalaxyUserNoRoleException("No role found for " + id + " in Galaxy "
				+ galaxyURL);
	}
}

package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import java.util.Objects;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;

/**
 * A GalaxyAccount object for storing credentials for an account in Galaxy.
 * 
 * 
 */
public class GalaxyAccountEmail implements UploaderAccountName {
	@NotNull(message = "{galaxy.user.email.notnull}")
	@Size(min = 5, message = "{galaxy.user.email.size}")
	@Email(message = "{galaxy.user.email.invalid}")
	private String galaxyAccountEmail;

	/**
	 * Builds a new GalaxyAccountEmail with the passed email address.
	 * @param galaxyAccountEmail  The email address for a user in Galaxy.
	 */
	public GalaxyAccountEmail(String galaxyAccountEmail) {
		this.galaxyAccountEmail = galaxyAccountEmail;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return galaxyAccountEmail;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return galaxyAccountEmail;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(galaxyAccountEmail);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalaxyAccountEmail other = (GalaxyAccountEmail) obj;
		
		return Objects.equals(this.galaxyAccountEmail, other.galaxyAccountEmail);
	}
}

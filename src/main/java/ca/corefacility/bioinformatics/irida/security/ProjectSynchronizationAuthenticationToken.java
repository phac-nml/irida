package ca.corefacility.bioinformatics.irida.security;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.ImmutableSet;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectSynchronizationService;

/**
 * An authentication token used to identify when an action is being taken during
 * project synchronization.
 * 
 * @see ProjectSynchronizationService
 */
public class ProjectSynchronizationAuthenticationToken extends PreAuthenticatedAuthenticationToken {

	/**
	 * Create a new ProjectSynchronizationAuthenticationToken for a given
	 * {@link User}.
	 * 
	 * @param user
	 *            the {@link User} to set in the auth token
	 */
	public ProjectSynchronizationAuthenticationToken(User user) {
		super(user, null, ImmutableSet.of(user.getSystemRole()));
	}

}

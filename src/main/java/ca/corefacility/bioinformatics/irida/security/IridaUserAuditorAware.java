package ca.corefacility.bioinformatics.irida.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * {@link AuditorAware} implementation for getting the user id of the logged in
 * {@link User}
 */
@Component
public class IridaUserAuditorAware implements AuditorAware<Long> {

	public IridaUserAuditorAware() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof User)) {
			return null;
		}

		User principal = (User) authentication.getPrincipal();

		return principal.getId();
	}

}

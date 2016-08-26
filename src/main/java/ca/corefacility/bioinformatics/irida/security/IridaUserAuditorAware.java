package ca.corefacility.bioinformatics.irida.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.User;

@Component
public class IridaUserAuditorAware implements AuditorAware<Long> {

	public IridaUserAuditorAware() {
	}

	@Override
	public Long getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}

		User principal = (User) authentication.getPrincipal();

		return principal.getId();
	}

}

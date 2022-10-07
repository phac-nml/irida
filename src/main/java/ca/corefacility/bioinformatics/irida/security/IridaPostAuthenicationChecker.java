package ca.corefacility.bioinformatics.irida.security;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import ca.corefacility.bioinformatics.irida.model.user.Role;

/**
 * Expired credentials should be ignored when a user is trying to change their password.
 */
public class IridaPostAuthenicationChecker implements UserDetailsChecker {
	private static final Logger logger = LoggerFactory.getLogger(IridaPostAuthenicationChecker.class);

	@Override
	public void check(UserDetails toCheck) {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (StackTraceElement frame : stack) {
			if (frame.getClassName().contains("UserService") && frame.getMethodName().equals("changePassword")) {
				logger.debug("Ignoring expired credentials because the user is trying to change their password.");
				return;
			}
		}

		logger.trace("Proceeding with checking expired credentials; user is not trying to change their password.");

		if (!toCheck.isCredentialsNonExpired()) {
			throw new CredentialsExpiredException("User credentials have exprired.");
		}

		logger.trace("Proceeding with checking if a ROLE_SEQUENCER user is attempting to login to the IRIDA UI.");

		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		// Don't allow ROLE_SEQUENCER to authenticate with login page. They should only be accessing IRIDA via API
		if (toCheck.getAuthorities().contains(Role.ROLE_SEQUENCER) && request.getRequestURI().endsWith("/login")) {
			logger.debug("Sequencer user: " + toCheck.getUsername() + " attempting to login to IRIDA UI.");
			throw new SequencerUILoginException("Sequencer should not be able to interact with IRIDA UI");
		}
	}

}

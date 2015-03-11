package ca.corefacility.bioinformatics.irida.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * Expired credentials should be ignored when a user is trying to change their
 * password.
 * 
 * 
 */
public class IgnoreExpiredCredentialsForPasswordChangeChecker implements UserDetailsChecker {
	private static final Logger logger = LoggerFactory
			.getLogger(IgnoreExpiredCredentialsForPasswordChangeChecker.class);

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
	}

}

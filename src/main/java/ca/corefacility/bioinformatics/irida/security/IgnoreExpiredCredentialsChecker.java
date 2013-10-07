package ca.corefacility.bioinformatics.irida.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * Expired credentials should be ignored in certain cases.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class IgnoreExpiredCredentialsChecker implements UserDetailsChecker {
	private static final Logger logger = LoggerFactory.getLogger(IgnoreExpiredCredentialsChecker.class);

	@Override
	public void check(UserDetails toCheck) {
		logger.debug("Ignoring expired credentials.");
	}

}

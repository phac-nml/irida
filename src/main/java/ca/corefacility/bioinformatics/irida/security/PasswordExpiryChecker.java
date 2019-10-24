package ca.corefacility.bioinformatics.irida.security;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Checks whether a user's password has changed within the configured expiry time.
 */
public class PasswordExpiryChecker implements UserDetailsChecker {
	private static final Logger logger = LoggerFactory.getLogger(PasswordExpiryChecker.class);

	private UserRepository userRepository;

	private int passwordExpiryInDays;

	/**
	 * Build a {@link PasswordExpiryChecker} with a given {@link UserRepository} and configured password expiry date
	 *
	 * @param userRepository       a {@link UserRepository}
	 * @param passwordExpiryInDays number of days until a password expires
	 */
	public PasswordExpiryChecker(UserRepository userRepository, int passwordExpiryInDays) {
		this.userRepository = userRepository;
		this.passwordExpiryInDays = passwordExpiryInDays;
	}

	/**
	 * Checks if the given {@link UserDetails} password has expired.
	 */
	@Override
	public void check(UserDetails toCheck) {
		User user = userRepository.loadUserByUsername(toCheck.getUsername());

		Revisions<Integer, User> revisions = userRepository.findRevisions(user.getId());

		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);

		cal.add(Calendar.DAY_OF_MONTH, -passwordExpiryInDays);
		Date expiryDate = cal.getTime();
		LocalDateTime localExpiryDate = LocalDateTime.ofInstant(expiryDate.toInstant(),
                                             ZoneId.systemDefault());

		User oldUser = null;

		for (Revision<Integer, User> rev : revisions) {

			logger.trace("Checking old user with date of " + rev.getRevisionDate());

			LocalDateTime revDate = rev.getRevisionDate().orElse(null);
			// if revision date is older than the expiry date we can stop looking
			if (revDate.isBefore(localExpiryDate)) {
				oldUser = rev.getEntity();
				break;
			}
		}

		/*
		If oldUser is null, the user isn't old enough to reset.  If the passwords are equal, they haven't been changed in the expiry time.
		 */
		if (oldUser != null && oldUser.getPassword().equals(user.getPassword())) {
			logger.warn("Credentials for user " + user.getUsername() + " have expired.");
			throw new CredentialsExpiredException("Credentials have expired.");
		}

	}
}

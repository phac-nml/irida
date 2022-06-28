package ca.corefacility.bioinformatics.irida.security.permissions.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.SequencingRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Permission allowing a user to read a {@link SequencingRun}
 */
@Component
public class ReadSequencingRunPermission extends RepositoryBackedPermission<SequencingRun, Long> {

	private static String PERMISSION_PROVIDED = "canReadSequencingRun";

	private static final Logger logger = LoggerFactory.getLogger(ReadSequencingRunPermission.class);

	private UserRepository userRepository;

	@Autowired
	public ReadSequencingRunPermission(SequencingRunRepository sequencingRunRepository, UserRepository userRepository) {
		super(SequencingRun.class, Long.class, sequencingRunRepository);
		this.userRepository = userRepository;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean customPermissionAllowed(Authentication authentication, SequencingRun targetDomainObject) {
		User user = userRepository.loadUserByUsername(authentication.getName());

		logger.trace("Checking if [" + authentication + "] can read [" + targetDomainObject + "]");

		if (user.getSystemRole().equals(Role.ROLE_SEQUENCER) || user.getSystemRole().equals(Role.ROLE_TECHNICIAN)) {
			logger.trace(
					"Permission GRANTED: " + user + " is a sequencer or technician so can read" + targetDomainObject);
			return true;
		}

		if (targetDomainObject.getUser() != null && targetDomainObject.getUser().equals(user)) {
			logger.trace("Permission GRANTED: " + user + " is the owner of " + targetDomainObject);
			return true;
		}

		logger.trace("Permission DENIED: " + user + " is NOT the owner of " + targetDomainObject);

		return false;
	}

}

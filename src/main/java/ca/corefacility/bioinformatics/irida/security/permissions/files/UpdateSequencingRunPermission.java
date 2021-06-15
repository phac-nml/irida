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
 * Permission checking if a user is the owner of a {@link SequencingRun} or if
 * they are ROLE_SEQUENCER
 */
@Component
public class UpdateSequencingRunPermission extends RepositoryBackedPermission<SequencingRun, Long> {

	private static final String PERMISSION_PROVIDED = "canUpdateSequencingRun";

	private static final Logger logger = LoggerFactory.getLogger(UpdateSequencingRunPermission.class);

	private UserRepository userRepository;

	/**
	 * Construct an instance of {@link UpdateSequencingRunPermission}.
	 * 
	 * @param sequencingRunRepository
	 *            a {@link SequencingRunRepository}
	 * @param userRepository
	 *            a {@link UserRepository}
	 */
	@Autowired
	public UpdateSequencingRunPermission(SequencingRunRepository sequencingRunRepository,
			UserRepository userRepository) {
		super(SequencingRun.class, Long.class, sequencingRunRepository);
		this.userRepository = userRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean customPermissionAllowed(Authentication authentication, SequencingRun targetDomainObject) {
		User user = userRepository.loadUserByUsername(authentication.getName());

		logger.trace("Checking if [" + authentication + "] can modify [" + targetDomainObject + "]");

		if (user.getSystemRole().equals(Role.ROLE_SEQUENCER)) {
			logger.trace("Permission GRANTED: " + user + " is a sequencer so can update " + targetDomainObject);
			return true;
		}

		if (targetDomainObject.getUser() != null && targetDomainObject.getUser().equals(user)) {
			logger.trace("Permission GRANTED: " + user + " is the owner of " + targetDomainObject);
			return true;
		}

		logger.trace("Permission DENIED: " + user + " is NOT the owner of " + targetDomainObject);

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}

package ca.corefacility.bioinformatics.irida.security.permissions.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Confirms if a {@link User} can update a {@link AnalysisSubmission}.
 * 
 *
 */
@Component
public class UpdateAnalysisSubmissionPermission extends RepositoryBackedPermission<AnalysisSubmission, Long> {

	private static final Logger logger = LoggerFactory.getLogger(UpdateAnalysisSubmissionPermission.class);
	private static final String PERMISSION_PROVIDED = "canUpdateAnalysisSubmission";

	private UserRepository userRepository;

	/**
	 * Constructs a new {@link UpdateAnalysisSubmissionPermission} with the given
	 * information.
	 * 
	 * @param analysisSubmissionRepository
	 *            A {@link AnalysisSubmissionRepository}.
	 * @param userRepository
	 *            A {@link UserRepository}.
	 */
	@Autowired
	public UpdateAnalysisSubmissionPermission(AnalysisSubmissionRepository analysisSubmissionRepository,
			UserRepository userRepository) {
		super(AnalysisSubmission.class, Long.class, analysisSubmissionRepository);
		this.userRepository = userRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean customPermissionAllowed(Authentication authentication, AnalysisSubmission analysisSubmission) {
		logger.trace("Testing permission for [" + authentication + "] on analysis submission [" + analysisSubmission
				+ "]");
		User u = userRepository.loadUserByUsername(authentication.getName());

		if (analysisSubmission.getSubmitter().equals(u)) {
			logger.trace("Permission GRANTED for [" + authentication + "] on analysis submission ["
					+ analysisSubmission + "]");
			return true;
		}

		logger.trace("Permission DENIED for [" + authentication + "] on analysis submission [" + analysisSubmission
				+ "]");
		return false;
	}
}

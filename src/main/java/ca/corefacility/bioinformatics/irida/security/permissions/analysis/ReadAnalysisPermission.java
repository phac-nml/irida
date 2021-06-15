package ca.corefacility.bioinformatics.irida.security.permissions.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Confirms if a {@link User} can read a {@link Analysis}.
 * 
 *
 */
@Component
public class ReadAnalysisPermission extends RepositoryBackedPermission<Analysis, Long> {

	private static final Logger logger = LoggerFactory
			.getLogger(ReadAnalysisPermission.class);
	private static final String PERMISSION_PROVIDED = "canReadAnalysis";

	private final UserRepository userRepository;
	private final AnalysisSubmissionRepository analysisSubmissionRepository;

	/**
	 * Constructs a new {@link ReadAnalysisPermission} with the given
	 * information.
	 * 
	 * @param analysisSubmissionRepository
	 *            A {@link AnalysisSubmissionRepository}.
	 * @param userRepository
	 *            A {@link UserRepository}.
	 * @param analysisRepository
	 * 			  A {@link AnalysisRepository}
	 */
	@Autowired
	public ReadAnalysisPermission(final AnalysisRepository analysisRepository,
			final UserRepository userRepository,
			final AnalysisSubmissionRepository analysisSubmissionRepository) {
		super(Analysis.class, Long.class, analysisRepository);
		this.userRepository = userRepository;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
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
	protected boolean customPermissionAllowed(
			final Authentication authentication, final Analysis analysis) {
		logger.trace("Testing permission for [" + authentication
				+ "] on analysis  [" + analysis + "]");
		final User u = userRepository.loadUserByUsername(authentication
				.getName());
		final AnalysisSubmission analysisSubmission = analysisSubmissionRepository
				.findByAnalysis(analysis);

		if (analysisSubmission.getSubmitter().equals(u)) {
			logger.trace("Permission GRANTED for [" + authentication
					+ "] on analysis submission [" + analysisSubmission + "]");
			return true;
		}

		logger.trace("Permission DENIED for [" + authentication
				+ "] on analysis submission [" + analysisSubmission + "]");
		return false;
	}
}

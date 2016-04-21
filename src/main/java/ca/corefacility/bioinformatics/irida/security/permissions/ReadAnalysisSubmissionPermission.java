package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Confirms if a {@link User} can read a {@link AnalysisSubmission}.
 * 
 *
 */
@Component
public class ReadAnalysisSubmissionPermission extends BasePermission<AnalysisSubmission, Long> {

	private static final Logger logger = LoggerFactory.getLogger(ReadAnalysisSubmissionPermission.class);
	private static final String PERMISSION_PROVIDED = "canReadAnalysisSubmission";

	private UserRepository userRepository;
	private final ReadSequencingObjectPermission seqObjectPermission;

	/**
	 * Constructs a new {@link ReadAnalysisSubmissionPermission} with the given
	 * information.
	 * 
	 * @param analysisSubmissionRepository
	 *            A {@link AnalysisSubmissionRepository}.
	 * @param userRepository
	 *            A {@link UserRepository}.
	 * @param seqObjectPermission
	 *            {@link ReadSequencingObjectPermission} to test if the
	 *            {@link AnalysisSubmission} is part of an automated assembly
	 */
	@Autowired
	public ReadAnalysisSubmissionPermission(AnalysisSubmissionRepository analysisSubmissionRepository,
			UserRepository userRepository, ReadSequencingObjectPermission seqObjectPermission) {
		super(AnalysisSubmission.class, Long.class, analysisSubmissionRepository);
		this.userRepository = userRepository;
		this.seqObjectPermission = seqObjectPermission;
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

		/*
		 * If the user isn't set it might be an automated submission. Check if
		 * this analysis is the auto assembly for a file and if they can read
		 * the file
		 */
		Set<SequenceFilePair> pairedInputFiles = analysisSubmission.getPairedInputFiles();

		boolean anyMatch = pairedInputFiles.stream().filter(o -> o.getAutomatedAssembly().equals(analysisSubmission))
				.anyMatch(p -> seqObjectPermission.customPermissionAllowed(authentication, p));

		if (anyMatch) {
			logger.trace("Permission GRANTED for [" + authentication + "] on analysis submission ["
					+ analysisSubmission + "]");
			return true;
		}

		logger.trace("Permission DENIED for [" + authentication + "] on analysis submission [" + analysisSubmission
				+ "]");
		return false;
	}
}

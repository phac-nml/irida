package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.ProjectAnalysisSubmissionJoinRepository;
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
	private final ProjectAnalysisSubmissionJoinRepository pasRepository;
	private final ReadProjectPermission readProjectPermission;

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
	 * @param pasRepository
	 *            A {@link ProjectAnalysisSubmissionJoinRepository} to check if
	 *            a submission has been shared with a project
	 * @param readProjectPermission
	 *            {@link ReadProjectPermission} to check if a user can read a
	 *            project where a submission has been shared
	 */
	@Autowired
	public ReadAnalysisSubmissionPermission(AnalysisSubmissionRepository analysisSubmissionRepository,
			UserRepository userRepository, ReadSequencingObjectPermission seqObjectPermission,
			ProjectAnalysisSubmissionJoinRepository pasRepository, ReadProjectPermission readProjectPermission) {
		super(AnalysisSubmission.class, Long.class, analysisSubmissionRepository);
		this.userRepository = userRepository;
		this.seqObjectPermission = seqObjectPermission;
		this.pasRepository = pasRepository;
		this.readProjectPermission = readProjectPermission;
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
		logger.trace(
				"Testing permission for [" + authentication + "] on analysis submission [" + analysisSubmission + "]");
		User u = userRepository.loadUserByUsername(authentication.getName());

		if (analysisSubmission.getSubmitter().equals(u)) {
			logger.trace("Permission GRANTED for [" + authentication + "] on analysis submission [" + analysisSubmission
					+ "]");
			return true;
		}

		/*
		 * If the user isn't set it might be shared to a project they have
		 * access to. Check the shared projects.
		 */

		List<ProjectAnalysisSubmissionJoin> projectsForSubmission = pasRepository
				.getProjectsForSubmission(analysisSubmission);
		boolean canReadProject = projectsForSubmission.stream()
				.anyMatch(p -> readProjectPermission.customPermissionAllowed(authentication, p.getSubject()));

		if (canReadProject) {
			logger.trace("Permission GRANTED for [" + authentication + "] on analysis submission [" + analysisSubmission
					+ "]");
			return true;
		}

		/*
		 * If the user isn't set it might be an automated submission. Check if
		 * this analysis is the auto assembly or sistr for a file and if they
		 * can read the file
		 */
		Set<SequenceFilePair> pairedInputFiles = analysisSubmission.getPairedInputFiles();

		boolean anyMatch = pairedInputFiles.stream().filter(o -> {
			AnalysisSubmission a = o.getAutomatedAssembly();
			AnalysisSubmission s = o.getSistrTyping();

			// check auto assembly
			boolean allowed = false;
			if (a != null) {
				allowed = a.equals(analysisSubmission);
			}

			// if not check sistr
			if (!allowed && s != null) {
				allowed = s.equals(analysisSubmission);
			}

			return allowed;
		}).anyMatch(p -> seqObjectPermission.customPermissionAllowed(authentication, p));

		if (anyMatch) {
			logger.trace("Permission GRANTED for [" + authentication + "] on analysis submission [" + analysisSubmission
					+ "]");
			return true;
		}

		logger.trace(
				"Permission DENIED for [" + authentication + "] on analysis submission [" + analysisSubmission + "]");
		return false;
	}
}

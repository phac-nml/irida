package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableSet;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionCleanupService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.AnalysisSubmissionServiceImpl;

/**
 * Implementation for a service to cleanup inconsistencies with {@link AnalysisSubmission}s.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class AnalysisSubmissionCleanupServiceImpl implements AnalysisSubmissionCleanupService {
	
	private static final Logger logger = LoggerFactory.getLogger(AnalysisSubmissionServiceImpl.class);
	
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	
	/**
	 * Defines a set of states that, if IRIDA was shutdown with a submission in
	 * one of these states, would need to be switched to an
	 * {@link AnalysisSubmission.ERROR} state.
	 */
	private static final Set<AnalysisState> inconsistentStates = ImmutableSet.of(AnalysisState.PREPARING,
			AnalysisState.SUBMITTING, AnalysisState.COMPLETING);
	private boolean ranSwitchInconsistentSubmissionsToError = false;
	
	/**
	 * Builds a new {@link AnalysisSubmissionCleanupServiceImpl} with the given information.
	 * @param analysisSubmissionService  The {@link AnalysisSubmissionService} to use.
	 */
	@Autowired
	public AnalysisSubmissionCleanupServiceImpl(AnalysisSubmissionRepository analysisSubmissionRepository) {
		this.analysisSubmissionRepository = analysisSubmissionRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int switchInconsistentSubmissionsToError() {
		if (ranSwitchInconsistentSubmissionsToError) {
			throw new RuntimeException("already ran this method once");
		} else {
			int numberSubmissionsSwitched = 0;
			ranSwitchInconsistentSubmissionsToError = true;

			for (AnalysisState state : inconsistentStates) {
				List<AnalysisSubmission> submissions = analysisSubmissionRepository.findByAnalysisState(state);
				for (AnalysisSubmission submission : submissions) {
					logger.error("AnalysisSubmission [id=" + submission.getId() + ", name=" + submission.getName()
							+ ", state=" + submission.getAnalysisState()
							+ "] left in inconsistent state.  Switching to " + AnalysisState.ERROR + ".");
					
					submission.setAnalysisState(AnalysisState.ERROR);
					analysisSubmissionRepository.save(submission);
					numberSubmissionsSwitched++;
				}
			}

			return numberSubmissionsSwitched;
		}
	}
}

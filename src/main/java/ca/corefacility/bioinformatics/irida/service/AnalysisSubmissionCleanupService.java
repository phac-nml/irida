package ca.corefacility.bioinformatics.irida.service;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;

/**
 * A service for cleaning up certaion aspects of {@link AnalysisSubmission}s.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface AnalysisSubmissionCleanupService {

	/**
	 * This examines all possible analysis submissions and moves any submissions
	 * not in a valid state to {@link AnalysisState.ERROR}. This is used on
	 * start up to clean up inconsistent submissions that weren't properly
	 * executed.
	 * 
	 * @return The number of submissions switched over to {@link AnalysisState.ERROR}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public int switchInconsistentSubmissionsToError();
}

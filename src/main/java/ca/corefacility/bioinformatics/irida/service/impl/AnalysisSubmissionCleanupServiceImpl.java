package ca.corefacility.bioinformatics.irida.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionCleanupService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

/**
 * Implementation for a service to cleanup inconsistencies with {@link AnalysisSubmission}s.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Service
public class AnalysisSubmissionCleanupServiceImpl implements AnalysisSubmissionCleanupService {
	
	private AnalysisSubmissionService analysisSubmissionService;
	
	/**
	 * Builds a new {@link AnalysisSubmissionCleanupServiceImpl} with the given information.
	 * @param analysisSubmissionService  The {@link AnalysisSubmissionService} to use.
	 */
	@Autowired
	public AnalysisSubmissionCleanupServiceImpl(AnalysisSubmissionService analysisSubmissionService) {
		this.analysisSubmissionService = analysisSubmissionService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int switchInconsistentSubmissionsToError() {
		return analysisSubmissionService.switchInconsistentSubmissionsToError();
	}
}

package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.PreparedWorkflow;

public interface AnalysisPreparationService<S extends AnalysisSubmission<?>, P extends PreparedWorkflow> {
	
	/**
	 * Prepares a workflow for an analysis given an analysis submission.
	 * @param analysisSubmission  The submission used to perform an analysis.
	 * @return  A PreparedWorkflow which can be submitted.
	 */
	public P prepareAnalysisWorkspace(S analysisSubmission);
}

package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * A service for AnalysisSubmissions.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface AnalysisSubmissionService extends CRUDService<String, AnalysisSubmission> {

	/**
	 * Given an analysis submission id, gets the state of this analysis. 
	 * @param analysisSubmissionId  The id of this analysis.
	 * @return  The state of this analysis.
	 * @throws EntityNotFoundException  If the corresponding analysis cannot be found.
	 */
	public AnalysisState getStateForAnalysis(String analysisSubmissionId) throws EntityNotFoundException;
}

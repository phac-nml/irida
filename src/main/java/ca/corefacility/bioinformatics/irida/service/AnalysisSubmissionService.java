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
	public AnalysisState getStateForAnalysisSubmission(String analysisSubmissionId) throws EntityNotFoundException;
	
	/**
	 * Sets the state for an analysis submission.
	 * @param analysisSubmissionId  The id of the analysis submission.
	 * @param state The state to set.
	 * @throws EntityNotFoundException  If the analysis submission corresponding to the given id does not exist.
	 */
	public void setStateForAnalysisSubmission(String analysisSubmissionId, AnalysisState state) throws EntityNotFoundException;
}

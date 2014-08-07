package ca.corefacility.bioinformatics.irida.service.analysis;

import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Used for executing workflows in a remote workflow manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface WorkflowManagementService {

	/**
	 * Submits the given analysis for processing on a remote workflow manager.
	 * @param analysisSubmission  A description of the analysis to submit.
	 * @return  An AnalysisExecution object with information about the submitted workflow.
	 * @throws WorkflowException  If there was an issue executing the workflow.
	 */
	public AnalysisExecution executeAnalysis(AnalysisSubmission analysisSubmission)
		throws WorkflowException;
	
	/**
	 * Given an analysis execution object gets an analysis result.
	 * @param analysisExecution  The AnalysisExecution object describing the analysis.
	 * @return  An Analysis object containing the results.
	 * @throws WorkflowException  If there was an error getting the result.
	 */
	public Analysis getAnalysisResults(AnalysisExecution analysisExecution)
		throws WorkflowException;
	
	/**
	 * Given an analysis execution object, gets the status for this analysis.
	 * @param analysisSubmission  The analysis submission object to find the status for.
	 * @return  A WorkflowStatus describing the status of the workflow.
	 * @throws WorkflowException  If an error occured getting the status.
	 */
	public WorkflowStatus getWorkflowStatus(AnalysisExecution analysisExecution) 
		throws WorkflowException;
	
	/**
	 * Given an analysis exeuction object, cancels the corresponding analysis.
	 * @param analysisExecution  The analysis execution to cancel.
	 * @throws WorkflowException  If an error occured canceling the analysis workflow. 
	 */
	public void cancelAnalysis(AnalysisExecution analysisExecution)
		throws WorkflowException;
}

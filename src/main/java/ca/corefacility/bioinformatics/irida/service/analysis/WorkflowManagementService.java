package ca.corefacility.bioinformatics.irida.service.analysis;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowException;
import ca.corefacility.bioinformatics.irida.model.workflow.RemoteAnalysisId;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

/**
 * Used for executing workflows in a remote workflow manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public interface WorkflowManagementService<ID extends RemoteAnalysisId, T extends AnalysisSubmission<?>> {

	/**
	 * Submits the given analysis for processing on a remote workflow manager.
	 * @param analysisSubmission  A description of the analysis to submit.
	 * @return  An id used to access information about the remote workflow.
	 * @throws WorkflowException  If there was an issue executing the workflow.
	 */
	public ID executeAnalysis(T analysisSubmission)
		throws ExecutionManagerException;
	
	/**
	 * Given an analysis execution object gets an analysis result.
	 * @param workflowId  An id for the remote workflow.
	 * @return  An Analysis object containing the results.
	 * @throws WorkflowException  If there was an error getting the result.
	 */
	public Analysis getAnalysisResults(ID workflowId)
		throws WorkflowException;
	
	/**
	 * Given an analysis execution object, gets the status for this analysis.
	 * @param workflowId  An id for the remote workflow.
	 * @return  A WorkflowStatus describing the status of the workflow.
	 * @throws WorkflowException  If an error occured getting the status.
	 */
	public WorkflowStatus getWorkflowStatus(ID workflowId) 
		throws ExecutionManagerException;
	
	/**
	 * Given an analysis exeuction object, cancels the corresponding analysis.
	 * @param workflowId  An id for the remote workflow.
	 * @throws WorkflowException  If an error occured canceling the analysis workflow. 
	 */
	public void cancelAnalysis(ID workflowId)
		throws WorkflowException;
}

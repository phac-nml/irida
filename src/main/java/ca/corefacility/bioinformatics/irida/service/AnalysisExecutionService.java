package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

public interface AnalysisExecutionService
	<A extends Analysis, S extends AnalysisSubmission<?>> {

	/**
	 * Executes the passed AnalysisSubmission in an execution manager.
	 * @param analysisSubmission  The submission to execute.
	 * @return  An AnalysisSubmission for the executed analysis.
	 * @throws ExecutionManagerException  If there was an issue executing the analysis.
	 */
	public S executeAnalysis(S analysisSubmission) throws ExecutionManagerException;
	
	/**
	 * Gets the status for the given submitted analysis.
	 * @param submittedAnalysis  The analysis to check the status in the execution manager.
	 * @return  A WorkflowStatus object containing the status of the analysis.
	 * @throws ExecutionManagerException  If there was an issue checking the status.
	 */
	public WorkflowStatus getWorkflowStatus(S submittedAnalysis)
			throws ExecutionManagerException;
	
	/**
	 * Gets the results of an analysis that was previously submitted.
	 * @param submittedAnalysis  An analysis that was previously submitted.
	 * @return  An Analysis object containing information about the particular analysis.
	 * @throws ExecutionManagerException  If there was an issue with the execution manager.
	 */
	public A getAnalysisResults(S submittedAnalysis)
			throws ExecutionManagerException;
}

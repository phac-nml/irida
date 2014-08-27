package ca.corefacility.bioinformatics.irida.service.analysis.execution;

import java.io.IOException;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Service for submission of analyses to an execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <A> The type of Analysis expected to be performed.
 * @param <S> The type of AnalysisSubmission to perform.
 */
public interface AnalysisExecutionService
	<A extends Analysis, S extends AnalysisSubmission<?>> {
	
	/**
	 * Prepares the given submission to be executed within an execution manager.  This
	 *  will persist the submission within the database.
	 * @param analysisSubmission  The submission to prepare.
	 * @return  An AnalysisSubmission for the prepared submission.
	 * @throws ExecutionManagerException  If there was an issue preparing the analysis.
	 */
	public S prepareSubmission(S analysisSubmission) throws ExecutionManagerException;

	/**
	 * Executes the passed prepared AnalysisSubmission in an execution manager.
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
	 * Downloads and saves the results of an analysis that was previously submitted from an execution manager.
	 * @param submittedAnalysis  An analysis that was previously submitted.
	 * @return  An Analysis object containing information about the particular analysis.
	 * @throws ExecutionManagerException  If there was an issue with the execution manager.
	 * @throws IOException If there was an error loading the analysis results from an execution manager.
	 */
	public A transferAnalysisResults(S submittedAnalysis)
			throws ExecutionManagerException, IOException;
}

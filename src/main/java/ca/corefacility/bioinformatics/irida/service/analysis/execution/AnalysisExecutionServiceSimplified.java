package ca.corefacility.bioinformatics.irida.service.analysis.execution;

import java.io.IOException;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Service for submission of analyses to an execution manager.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 */
public interface AnalysisExecutionServiceSimplified {

	/**
	 * Prepares the given {@link AnalysisSubmission} to be executed within an
	 * execution manager. This will persist the submission within the database.
	 * 
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission} to prepare.
	 * @return An {@link AnalysisSubmission} for the prepared submission.
	 * @throws ExecutionManagerException
	 *             If there was an issue preparing the analysis.
	 * @throws IridaWorkflowNotFoundException
	 *             If the workflow for this submission could not be found in
	 *             IRIDA.
	 * @throws IOException
	 *             If there was an issue reading in the workflow file.
	 */
	public AnalysisSubmission prepareSubmission(
			AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowNotFoundException,
			IOException;

	/**
	 * Executes the passed prepared {@link AnalysisSubmission} in an execution
	 * manager.
	 * 
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission} to execute.
	 * @return An {@link AnalysisSubmission} for the executed analysis.
	 * @throws ExecutionManagerException
	 *             If there was an issue executing the analysis.
	 * @throws IridaWorkflowNotFoundException
	 *             If the workflow for this submission could not be found in
	 *             IRIDA.
	 */
	public AnalysisSubmission executeAnalysis(
			AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowNotFoundException;

	/**
	 * Gets the status for the given submitted analysis.
	 * 
	 * @param submittedAnalysis
	 *            The {@link AnalysisSubmission} to check the status in the
	 *            execution manager.
	 * @return A WorkflowStatus object containing the status of the analysis.
	 * @throws ExecutionManagerException
	 *             If there was an issue checking the status.
	 */
	public WorkflowStatus getWorkflowStatus(AnalysisSubmission submittedAnalysis)
			throws ExecutionManagerException;

	/**
	 * Downloads and saves the results of an {@link Analysis} that was
	 * previously submitted from an execution manager.
	 * 
	 * @param submittedAnalysis
	 *            An {@link AnalysisSubmission} that was previously submitted.
	 * @return An {@link Analysis} object containing information about the
	 *         particular analysis.
	 * @throws ExecutionManagerException
	 *             If there was an issue with the execution manager.
	 * @throws IridaWorkflowNotFoundException
	 *             If the workflow for this submission could not be found in
	 *             IRIDA.
	 * @throws IOException
	 *             If there was an error loading the analysis results from an
	 *             execution manager.
	 */
	public Analysis transferAnalysisResults(AnalysisSubmission submittedAnalysis)
			throws ExecutionManagerException, IridaWorkflowNotFoundException,
			IOException;
}

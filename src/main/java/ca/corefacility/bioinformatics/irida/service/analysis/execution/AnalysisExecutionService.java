package ca.corefacility.bioinformatics.irida.service.analysis.execution;

import java.io.IOException;
import java.util.concurrent.Future;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Service for submission of {@link AnalysisSubmission}s to an execution
 * manager.
 * 
 */
public interface AnalysisExecutionService {
	
	/**
	 * Prepares the given {@link AnalysisSubmission} to be executed within an
	 * execution manager. This will persist the submission within the database.
	 * 
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission} to prepare.
	 * 
	 * @return A {@link Future} of type {@link AnalysisSubmission} which can be
	 *         used to access the prepared submission.
	 * @throws IridaWorkflowNotFoundException
	 *             If there was an issue getting a workflow.
	 * @throws IOException
	 *             If there was an issue reading the workflow.
	 * @throws ExecutionManagerException
	 *             If there was an issue preparing a workspace for the workflow.
	 */
	public Future<AnalysisSubmission> prepareSubmission(AnalysisSubmission analysisSubmission)
			throws IridaWorkflowNotFoundException, IOException, ExecutionManagerException;

	/**
	 * Executes the passed prepared {@link AnalysisSubmission} in an execution
	 * manager.
	 * 
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission} to execute.
	 * @return A {@link Future} with an {@link AnalysisSubmission} for the
	 *         analysis submitted.
	 * @throws ExecutionManagerException
	 *             If there was an exception submitting the analysis to the
	 *             execution manager.
	 * @throws IridaWorkflowException
	 *             If there was an issue with the IRIDA workflow.
	 * @throws IOException If there was an error reading some of the input files.
	 */
	public Future<AnalysisSubmission> executeAnalysis(AnalysisSubmission analysisSubmission)
			throws IridaWorkflowException, ExecutionManagerException, IOException;

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
	public GalaxyWorkflowStatus getWorkflowStatus(AnalysisSubmission submittedAnalysis) throws ExecutionManagerException;

	/**
	 * Downloads and saves the results of an {@link AnalysisSubmission} that was
	 * previously submitted from an execution manager.
	 * 
	 * @param submittedAnalysis
	 *            An {@link AnalysisSubmission} that was previously submitted.
	 * @return A {@link Future} with an {@link AnalysisSubmission} object
	 *         containing information about the particular analysis.
	 * @throws ExecutionManagerException
	 *             If there was an issue with the execution manager.
	 * @throws IridaWorkflowNotFoundException
	 *             If the workflow for this submission could not be found in
	 *             IRIDA.
	 * @throws IOException
	 *             If there was an error loading the analysis results from an
	 *             execution manager.
	 * @throws IridaWorkflowAnalysisTypeException
	 *             If there was an issue building an {@link Analysis} object.
	 */
	public Future<AnalysisSubmission> transferAnalysisResults(AnalysisSubmission submittedAnalysis)
			throws ExecutionManagerException, IridaWorkflowNotFoundException, IOException,
			IridaWorkflowAnalysisTypeException;

	/**
	 * Performs any post processing required for an {@link AnalysisSubmission}.  Usually this will be a sample updater implementation.
	 *
	 * @param analysisSubmission the {@link AnalysisSubmission} to process
	 * @return a Future {@link AnalysisSubmission}
	 */
	public Future<AnalysisSubmission> postProcessResults(AnalysisSubmission analysisSubmission);

	/**
	 * Cleans up any intermediate files in the execution manager for this
	 * submission.
	 * 
	 * @param analysisSubmission
	 *            The {@link AnalysisSubmission} to clean.
	 * @return The cleaned-up {@link AnalysisSubmission}.
	 * @throws ExecutionManagerException
	 *             If there was an issue with the execution manager.
	 */
	public Future<AnalysisSubmission> cleanupSubmission(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException;
	
	/**
	 * Get the {@link AnalysisExecutionService} capacity for running new jobs.
	 * This will return the number of open slots for running new workflows.
	 * 
	 * NOTE: This is not a hard limit. It just reports the number running vs
	 * configured maximum requested.
	 * 
	 * @return the number of available slots for running jobs
	 */
	public int getCapacity();
}

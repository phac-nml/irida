package ca.corefacility.bioinformatics.irida.service.analysis.workspace;

import java.io.IOException;
import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.PreparedWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines a service used to perform the tasks for execution of a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 */
public interface AnalysisWorkspaceServiceSimplified {

	/**
	 * Prepares the workspace for an analysis given an analysis submission.
	 * 
	 * @param analysisSubmission
	 *            The submission used to perform an analysis.
	 * @return A String identifiying the analysis workspace.
	 * @throws ExecutionManagerException
	 *             If there was an issue preparing the workflow workspace.
	 */
	public String prepareAnalysisWorkspace(AnalysisSubmission analysisSubmission) throws ExecutionManagerException;

	/**
	 * Prepares the files for a workflow for an analysis given an analysis
	 * submission.
	 * 
	 * @param analysisSubmission
	 *            The submission used to perform an analysis.
	 * @return A PreparedWorkflow which can be submitted.
	 * @throws ExecutionManagerException
	 *             If there was an issue preparing the workflow workspace.
	 * @throws IridaWorkflowNotFoundException
	 *             If the workflow passed to this analysis submission could not
	 *             be found.
	 */
	public PreparedWorkflow<?> prepareAnalysisFiles(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowNotFoundException;

	/**
	 * Gets an Analysis object containing the results for this analysis. This
	 * object is not persisted in the database.
	 * 
	 * @param analysisSubmission
	 *            The submission to get the results for.
	 * @param outputDirectory
	 *            A directory to store output files downloaded from this
	 *            analysis.
	 * @return An Analysis object containing the results.
	 * @throws ExecutionManagerException
	 *             If there was an error getting the results.
	 * @throws IridaWorkflowNotFoundException
	 *             If the workflow passed to this analysis submission could not
	 *             be found.
	 * @throws IOException
	 *             If there was an error when loading the results of an analysis
	 *             from Galaxy to a local file.
	 */
	public Analysis getAnalysisResults(AnalysisSubmission analysisSubmission, Path outputDirectory)
			throws ExecutionManagerException, IridaWorkflowNotFoundException, IOException;
}

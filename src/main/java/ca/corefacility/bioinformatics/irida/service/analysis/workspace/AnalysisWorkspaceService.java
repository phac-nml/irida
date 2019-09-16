package ca.corefacility.bioinformatics.irida.service.analysis.workspace;

import java.io.IOException;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowAnalysisTypeException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines a service used to perform the tasks for execution of a workflow.
 * 
 */
public interface AnalysisWorkspaceService {

	/**
	 * Prepares the workspace for an analysis given an analysis submission. This
	 * provides a remote location where files can be stored for analysis
	 * (creates a Galaxy History).
	 * 
	 * @param analysisSubmission
	 *            The submission used to perform an analysis.
	 * @return A String identifying the analysis workspace.
	 * @throws ExecutionManagerException
	 *             If there was an issue preparing the workflow workspace.
	 */
	public String prepareAnalysisWorkspace(AnalysisSubmission analysisSubmission) throws ExecutionManagerException;

	/**
	 * Uploads and prepares the files and other necessary data structures of a
	 * workflow for an analysis given an analysis submission.
	 * 
	 * @param analysisSubmission
	 *            The submission used to perform an analysis.
	 * @return A PreparedWorkflow which can be submitted.
	 * @throws ExecutionManagerException
	 *             If there was an issue preparing the workflow workspace.
	 * @throws IridaWorkflowException
	 *             If there was an issue with the IRIDA workflow.
	 * @throws IOException If there was an error reading some of the input files.            
	 */
	public PreparedWorkflowGalaxy prepareAnalysisFiles(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowException, IOException;

	/**
	 * Gets an Analysis object containing the results for this analysis. This
	 * object is not persisted in the database.
	 * 
	 * @param analysisSubmission
	 *            The submission to get the results for.
	 * @return An Analysis object containing the results.
	 * @throws ExecutionManagerException
	 *             If there was an error getting the results.
	 * @throws IridaWorkflowNotFoundException
	 *             If the workflow passed to this analysis submission could not
	 *             be found.
	 * @throws IOException
	 *             If there was an error when loading the results of an analysis
	 *             from Galaxy to a local file.
	 * @throws IridaWorkflowAnalysisTypeException
	 *             If there was an issue building an {@link Analysis} object.
	 */
	public Analysis getAnalysisResults(AnalysisSubmission analysisSubmission)
			throws ExecutionManagerException, IridaWorkflowNotFoundException, IOException,
			IridaWorkflowAnalysisTypeException;
}

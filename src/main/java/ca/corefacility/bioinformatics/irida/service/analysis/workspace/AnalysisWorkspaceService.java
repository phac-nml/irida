package ca.corefacility.bioinformatics.irida.service.analysis.workspace;

import java.io.IOException;
import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.PreparedWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines a service used to perform the tasks for execution of a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <S>  The AnalysisSubmission to handle.
 * @param <P>  The PreparedWorkflow to generate.
 * @param <A>  The Analysis object to return as a result.
 */
public interface AnalysisWorkspaceService<S extends AnalysisSubmission, P extends PreparedWorkflow<?>, A extends Analysis> {
	
	/**
	 * Prepares the workspace for an analysis given an analysis submission.
	 * @param analysisSubmission  The submission used to perform an analysis.
	 * @return  A String identifiying the analysis workspace.
	 * @throws ExecutionManagerException If there was an issue preparing the workflow workspace.
	 */
	public String prepareAnalysisWorkspace(S analysisSubmission) throws ExecutionManagerException;
	
	/**
	 * Prepares the files for a workflow for an analysis given an analysis submission.
	 * @param analysisSubmission  The submission used to perform an analysis.
	 * @return  A PreparedWorkflow which can be submitted.
	 * @throws ExecutionManagerException If there was an issue preparing the workflow workspace.
	 */
	public P prepareAnalysisFiles(S analysisSubmission) throws ExecutionManagerException;
	
	/**
	 * Gets an Analysis object containing the results for this analysis.  This object is not persisted
	 *  in the database.
	 * @param analysisSubmission  The submission to get the results for.
	 * @param outputDirectory A directory to store output files downloaded from this analysis.
	 * @return  An Analysis object containing the results.
	 * @throws ExecutionManagerException  If there was an error getting the results.
	 * @throws IOException  If there was an error when loading the results of an analysis from Galaxy 
	 *  to a local file.
	 */
	public A getAnalysisResults(S analysisSubmission, Path outputDirectory) throws ExecutionManagerException, IOException;
}

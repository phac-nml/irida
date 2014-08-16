package ca.corefacility.bioinformatics.irida.service.analysis.execution;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.workflow.PreparedWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines a service used to perform the tasks for execution of a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <S>  The AnalysisSubmission to handle.
 * @param <P>  The PreparedWorkflow to generate.
 */
public interface AnalysisExecutionService<S extends AnalysisSubmission<?>, P extends PreparedWorkflow> {
	
	/**
	 * Prepares a workflow for an analysis given an analysis submission.
	 * @param analysisSubmission  The submission used to perform an analysis.
	 * @return  A PreparedWorkflow which can be submitted.
	 * @throws ExecutionManagerException If there was an issue preparing the workflow workspace.
	 */
	public P prepareAnalysisWorkspace(S analysisSubmission) throws ExecutionManagerException;
}

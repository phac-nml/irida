package ca.corefacility.bioinformatics.irida.model.workflow;

/**
 * A Workflow that has been prepared for execution.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */

/**
 * A Workflow that has been prepared for execution.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <AnalysisIdType> The type of remote analysis id.
 * @param <WorkflowInputsType> The type of WorkflowInputsGeneric.
 */
public interface PreparedWorkflow<AnalysisIdType extends RemoteAnalysisId, WorkflowInputsType extends WorkflowInputsGeneric> {
	
	/**
	 * Gets the RemoteAnalysisId this workflow.
	 * @return  The RemoteAnalysisId for this workflow.
	 */
	public AnalysisIdType getRemoteAnalysisId();
	
	/**
	 * Gets the inputs to a workflow.
	 * @return The inputs to a workflow.
	 */
	public WorkflowInputsType getWorkflowInputs();
}

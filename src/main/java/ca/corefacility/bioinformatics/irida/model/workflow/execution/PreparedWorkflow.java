package ca.corefacility.bioinformatics.irida.model.workflow.execution;


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
public interface PreparedWorkflow<WorkflowInputsType extends WorkflowInputsGeneric> {
	
	/**
	 * Gets the analysis id this workflow.
	 * @return  The analysis id for this workflow.
	 */
	public String getRemoteAnalysisId();
	
	/**
	 * Gets the inputs to a workflow.
	 * @return The inputs to a workflow.
	 */
	public WorkflowInputsType getWorkflowInputs();
}

package ca.corefacility.bioinformatics.irida.model.workflow.execution;

/**
 * A Workflow that has been prepared for execution.
 *
 * @param <WorkflowInputsType> The type of WorkflowInputsGeneric.
 */
public interface PreparedWorkflow<WorkflowInputsType extends WorkflowInputsGeneric> {

	/**
	 * Gets the analysis id this workflow.
	 * 
	 * @return The analysis id for this workflow.
	 */
	public String getRemoteAnalysisId();

	/**
	 * Gets the id for a location used to store data for a workflow.
	 * 
	 * @return The id of a location used to store data for a workflow.
	 */
	public String getRemoteDataId();

	/**
	 * Gets the inputs to a workflow.
	 * 
	 * @return The inputs to a workflow.
	 */
	public WorkflowInputsType getWorkflowInputs();
}

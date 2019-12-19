package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInvocationInputs;

import ca.corefacility.bioinformatics.irida.model.workflow.execution.WorkflowInputsGeneric;

/**
 * Describes a set of workflow inputs for a Galaxy workflow.
 *
 */
public class WorkflowInputsGalaxy implements WorkflowInputsGeneric {

	private WorkflowInvocationInputs workflowInputs;
	
	/**
	 * Builds a new WorkflowInputsGalaxy to wrap around a WorkflowInputs.
	 * @param workflowInputs the inputs for this workflow.
	 */
	public WorkflowInputsGalaxy(WorkflowInvocationInputs workflowInputs) {
		this.workflowInputs = workflowInputs;
	}

	/**
	 * Gets the WorkflowInputs object.
	 * @return The WorkflowInputs object.
	 */
	public WorkflowInvocationInputs getInputsObject() {
		return workflowInputs;
	}
}

package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy;

import com.github.jmchilton.blend4j.galaxy.beans.WorkflowInputs;

import ca.corefacility.bioinformatics.irida.model.workflow.execution.WorkflowInputsGeneric;

/**
 * Describes a set of workflow inputs for a Galaxy workflow.
 *
 */
public class WorkflowInputsGalaxy implements WorkflowInputsGeneric {

	private WorkflowInputs workflowInputs;
	
	/**
	 * Builds a new WorkflowInputsGalaxy to wrap around a WorkflowInputs.
	 * @param workflowInputs
	 */
	public WorkflowInputsGalaxy(WorkflowInputs workflowInputs) {
		this.workflowInputs = workflowInputs;
	}

	/**
	 * Gets the WorkflowInputs object.
	 * @return The WorkflowInputs object.
	 */
	public WorkflowInputs getInputsObject() {
		return workflowInputs;
	}
}

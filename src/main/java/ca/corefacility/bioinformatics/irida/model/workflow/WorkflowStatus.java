package ca.corefacility.bioinformatics.irida.model.workflow;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines the status of a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowStatus {
	private WorkflowState state;
	private float percentComplete;
	
	/**
	 * Constructs a new WorkflowStatus with the given information.
	 * @param state  The state of the workflow.
	 * @param percentComplete  The percentage complete for the workflow.
	 */
	public WorkflowStatus(WorkflowState state, float percentComplete) {
		checkNotNull(state, "state is null");
		
		this.state = state;
		this.percentComplete = percentComplete;
	}

	/**
	 * Gets the state of the workflow.
	 * @return  The WorkflowState for this workflow.
	 */
	public WorkflowState getState() {
		return state;
	}

	/**
	 * Gets the percentage complete for this workflow.
	 * @return The percentage complete for this workflow.
	 */
	public float getPercentComplete() {
		return percentComplete;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "WorkflowStatus [state=" + state + ", percentComplete="
				+ percentComplete + "]";
	}
}
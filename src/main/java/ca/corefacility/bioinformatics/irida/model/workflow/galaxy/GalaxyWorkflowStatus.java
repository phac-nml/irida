package ca.corefacility.bioinformatics.irida.model.workflow.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines the status of a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowStatus {
	private GalaxyWorkflowState state;
	private float percentComplete;
	
	/**
	 * Constructs a new {@link GalaxyWorkflowStatus} with the given information.
	 * @param state  The state of the workflow.
	 * @param percentComplete  The percentage complete for the workflow.
	 */
	public GalaxyWorkflowStatus(GalaxyWorkflowState state, float percentComplete) {
		checkNotNull(state, "state is null");
		
		this.state = state;
		this.percentComplete = percentComplete;
	}

	/**
	 * Gets the state of the workflow.
	 * @return  The {@link GalaxyWorkflowState} for this workflow.
	 */
	public GalaxyWorkflowState getState() {
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
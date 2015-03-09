package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;

/**
 * Defines the status of a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyWorkflowStatus {
	private GalaxyWorkflowState state;
	private Map<String, List<String>> stateIds;
	
	/**
	 * Constructs a new {@link GalaxyWorkflowStatus} with the given information.
	 * @param state  The state of the workflow.
	 * @param stateIds  A map of state ids for this workflow.
	 */
	public GalaxyWorkflowStatus(GalaxyWorkflowState state, Map<String, List<String>> stateIds) {
		checkNotNull(state, "state is null");
		checkNotNull(stateIds, "stateIds are null");
		
		this.state = state;
		this.stateIds = stateIds;
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
		return getPercentComplete(stateIds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "WorkflowStatus [state=" + state + ", percentComplete="
				+ getPercentComplete() + "]";
	}
	
	/**
	 * Count the total number of history items for a given list of state ids.
	 * @param stateIds  A list of state ids to search through.
	 * @return  The total number of history items.
	 */
	private int countTotalHistoryItems(Map<String, List<String>> stateIds) {
		return stateIds.values().stream().mapToInt(List::size).sum();
	}
	
	/**
	 * Count the total number of history items within the given workflow state.
	 * @param stateIds  The list of history items to search through.
	 * @param state  A state to search for.
	 * @return  The number of history items in this state.
	 */
	private int countHistoryItemsInState(Map<String, List<String>> stateIds, GalaxyWorkflowState state) {
		return stateIds.get(state.toString()).size();
	}
	
	/**
	 * Gets the percentage completed running of items within the given list of history items.
	 * @param stateIds  The list of history items.
	 * @return  The percent of history items that are finished running.
	 */
	private float getPercentComplete(Map<String, List<String>> stateIds) {
		return 100.0f*(countHistoryItemsInState(stateIds, GalaxyWorkflowState.OK)/(float)countTotalHistoryItems(stateIds));
	}
	
	public static GalaxyWorkflowStatus buildStatusFromHistoryDetails(HistoryDetails historyDetails) {
		checkNotNull(historyDetails, "historyDetails is null");
		checkNotNull(historyDetails.getState(), "historyDetails.getState() is null");
		checkNotNull(historyDetails.getStateIds(), "historyDetails.getStateIds() is null");
		
		GalaxyWorkflowState workflowState = GalaxyWorkflowState.stringToState(historyDetails.getState());
				
		GalaxyWorkflowStatus galaxyWorkflowStatus = new GalaxyWorkflowStatus(workflowState, historyDetails.getStateIds());
		
		return galaxyWorkflowStatus;
	}
}
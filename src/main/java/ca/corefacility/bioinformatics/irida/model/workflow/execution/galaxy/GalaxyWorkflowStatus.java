package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmchilton.blend4j.galaxy.beans.HistoryDetails;
import com.google.common.collect.Sets;

/**
 * Defines the status of a workflow.
 *
 */
public class GalaxyWorkflowStatus {

	private final GalaxyWorkflowState state;
	private final Map<GalaxyWorkflowState, Set<String>> stateIds;
	private final int totalWorkflowItems;

	/**
	 * Constructs a new {@link GalaxyWorkflowStatus} with the given information.
	 * 
	 * @param state
	 *            The state of the workflow.
	 * @param stateIds
	 *            A map of {@link GalaxyWorkflowState} to a set of ids in this
	 *            state for this workflow.
	 */
	public GalaxyWorkflowStatus(GalaxyWorkflowState state, Map<GalaxyWorkflowState, Set<String>> stateIds) {
		checkNotNull(state, "state is null");
		checkNotNull(stateIds, "stateIds are null");

		this.state = state;
		this.stateIds = stateIds;
		this.totalWorkflowItems = stateIds.values().stream().mapToInt(Set::size).sum();
	}

	/**
	 * Whether or not this workflow has completed successfully.
	 * 
	 * @return True if this workflow has completed successfully, false
	 *         otherwise.
	 */
	public boolean completedSuccessfully() {
		return state.equals(GalaxyWorkflowState.OK);
	}

	/**
	 * Whether or not this workflow is in an error state.
	 * 
	 * @return True if this workflow has completed successfully, false
	 *         otherwise.
	 */
	public boolean errorOccurred() {
		return state.equals(GalaxyWorkflowState.ERROR) || countHistoryItemsInState(GalaxyWorkflowState.ERROR) > 0
				|| state.equals(GalaxyWorkflowState.FAILED_METADATA)
				|| countHistoryItemsInState(GalaxyWorkflowState.FAILED_METADATA) > 0;
	}

	/**
	 * Whether or not this workflow is still running.
	 * 
	 * @return True if this workflow is still running, false otherwise.
	 */
	public boolean isRunning() {
		return Sets.newHashSet(GalaxyWorkflowState.UPLOAD, GalaxyWorkflowState.QUEUED, GalaxyWorkflowState.RUNNING,
				GalaxyWorkflowState.PAUSED, GalaxyWorkflowState.SETTING_METADATA, GalaxyWorkflowState.RESUBMITTED)
				.contains(state);
	}

	/**
	 * Gets the state of the workflow.
	 * 
	 * @return The {@link GalaxyWorkflowState} for this workflow.
	 */
	public GalaxyWorkflowState getState() {
		return state;
	}

	/**
	 * Gets the percentage complete for this workflow.
	 * 
	 * @return The percentage complete for this workflow.
	 */
	public float getPercentComplete() {
		return 100.0f * (countHistoryItemsInState(GalaxyWorkflowState.OK) / (float) totalWorkflowItems);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "WorkflowStatus [state=" + state + ", percentComplete=" + getPercentComplete() + "]";
	}

	/**
	 * Count the total number of history items within the given workflow state.
	 * 
	 * @param state
	 *            A state to search for.
	 * @return The number of history items in this state.
	 */
	private int countHistoryItemsInState(GalaxyWorkflowState state) {
		return stateIds.get(state).size();
	}

	/**
	 * Constructs a new {@link GalaxyWorkflowStatusBuilder}.
	 * 
	 * @param historyDetails
	 *            The HistoryDetails used to build a
	 *            {@link GalaxyWorkflowStatus}.
	 * 
	 * @return A new {@link GalaxyWorkflowStatusBuilder}.
	 */
	public static GalaxyWorkflowStatusBuilder builder(HistoryDetails historyDetails) {
		return new GalaxyWorkflowStatusBuilder(historyDetails);
	}

	/**
	 * Used to build a {@link GalaxyWorkflowStatus} from the underlying Galaxy
	 * objects.
	 */
	public static class GalaxyWorkflowStatusBuilder {

		private static final Set<GalaxyWorkflowState> ALL_STATES = Sets.newHashSet(GalaxyWorkflowState.values());
		private static final Set<GalaxyWorkflowState> NO_UNKNOWN_STATES = Sets.newHashSet(GalaxyWorkflowState.values());

		static {
			NO_UNKNOWN_STATES.remove(GalaxyWorkflowState.UNKNOWN);
		}

		private static final Logger logger = LoggerFactory.getLogger(GalaxyWorkflowStatusBuilder.class);

		private HistoryDetails historyDetails;

		/**
		 * Constructs a new {@link GalaxyWorkflowStatusBuilder}.
		 * 
		 * @param historyDetails
		 *            The HistoryDetails to build the status from.
		 */
		public GalaxyWorkflowStatusBuilder(HistoryDetails historyDetails) {
			checkNotNull(historyDetails, "historyDetails is null");
			this.historyDetails = historyDetails;
		}

		/**
		 * Builds a new {@link GalaxyWorkflowStatus} from the underlying history
		 * details.
		 * 
		 * @return A new {@link GalaxyWorkflowStatus}.
		 */
		public GalaxyWorkflowStatus build() {
			checkNotNull(historyDetails, "historyDetails is null");
			checkNotNull(historyDetails.getState(), "historyDetails.getState() is null");
			checkNotNull(historyDetails.getStateIds(), "historyDetails.getStateIds() is null");

			GalaxyWorkflowState workflowState = GalaxyWorkflowState.stringToState(historyDetails.getState());
			Map<GalaxyWorkflowState, Set<String>> stateIdsMap = createStateIdsMap(historyDetails);

			checkArgument(stateIdsMap.keySet().equals(ALL_STATES) || stateIdsMap.keySet().equals(NO_UNKNOWN_STATES),
					"invalid states: " + stateIdsMap.keySet());

			return new GalaxyWorkflowStatus(workflowState, stateIdsMap);
		}

		/**
		 * Converts the given HistoryDetails map (using Strings) to a map using
		 * the GalaxyWorkflowState enum.
		 * 
		 * @param historyDetails
		 *            The HistoryDetails object to convert.
		 * @return A map using the GalaxyWorkflowState enum.
		 */
		private Map<GalaxyWorkflowState, Set<String>> createStateIdsMap(HistoryDetails historyDetails) {
			Map<GalaxyWorkflowState, Set<String>> stateIdsMap = new HashMap<>();

			Map<String, List<String>> stateIds = historyDetails.getStateIds();
			for (String stateString : stateIds.keySet()) {
				GalaxyWorkflowState workflowState = GalaxyWorkflowState.stringToState(stateString);
				Set<String> idSet;

				if (stateIdsMap.containsKey(workflowState)) {
					logger.debug("State " + workflowState + " already exists");
					idSet = stateIdsMap.get(workflowState);
				} else {
					idSet = new HashSet<>();
					stateIdsMap.put(workflowState, idSet);
				}

				idSet.addAll(stateIds.get(stateString));
			}

			return stateIdsMap;
		}
	}
}
package ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines the state of a workflow. Based off of states defined within Galaxy.
 * 
 * @see <a href=
 *      "https://github.com/galaxyproject/galaxy/blob/21375c6b821b85863a6c95f4ed1ebd6a217495f1/lib/galaxy/model/__init__.py#L3410-L3427">Galaxy
 *      Dataset Model</a>
 * @see <a href=
 *      "https://github.com/galaxyproject/galaxy/blob/21375c6b821b85863a6c95f4ed1ebd6a217495f1/lib/galaxy/webapps/galaxy/api/histories.py#L181-L192">Galaxy
 *      API show_history</a>
 * @see <a href=
 *      "https://github.com/jmchilton/blend4j/blob/c5e3f157d402950a843d4e395e1daf889945d587/src/main/java/com/github/jmchilton/blend4j/galaxy/beans/HistoryDetails.java">HistoryDetails
 *      in blend4j</a>
 */
public enum GalaxyWorkflowState {
	NEW("new"),
	UPLOAD("upload"),
	QUEUED("queued"),
	RUNNING("running"),

	/**
	 * The workflow will be in this state when everything is complete.
	 */
	OK("ok"),

	EMPTY("empty"),
	ERROR("error"),
	DISCARDED("discarded"),
	PAUSED("paused"),
	SETTING_METADATA("setting_metadata"),
	FAILED_METADATA("failed_metadata"),
	DEFERRED("deferred"),
	RESUBMITTED("resubmitted");

	private static Map<String, GalaxyWorkflowState> stateMap = new HashMap<>();
	private String stateString;

	/*
	 * Sets of a Map used to convert a string to a WorkflowState
	 */
	static {
		for (GalaxyWorkflowState state : GalaxyWorkflowState.values()) {
			stateMap.put(state.toString(), state);
		}
	}

	private GalaxyWorkflowState(String stateString) {
		this.stateString = stateString;
	}

	/**
	 * Given a string defining a state, converts this to a {@link GalaxyWorkflowState}.
	 * 
	 * @param stateString The string defining the state.
	 * @return A {@link GalaxyWorkflowState} for the corresponding state.
	 */
	public static GalaxyWorkflowState stringToState(String stateString) {
		checkNotNull(stateString, "stateString is null");

		GalaxyWorkflowState state = stateMap.get(stateString);
		checkNotNull(state, "Unknown state " + stateString);

		return state;
	}

	@Override
	public String toString() {
		return stateString;
	}
}

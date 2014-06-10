package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the state of a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public enum WorkflowState {
	OK("ok"),
	RUNNING("running"),
	QUEUED("queued"),
	UNKNOWN("unknown");
	
	private static Map<String, WorkflowState> stateMap = new HashMap<>();
	private String stateString;
	
	/**
	 * Sets of a Map used to convert a string to a WorkflowState
	 */
	static {
		for (WorkflowState state : WorkflowState.values()) {
			stateMap.put(state.toString(), state);
		}
	}
	
	private WorkflowState(String stateString){
		this.stateString = stateString;
	}
	
	/**
	 * Given a string defining a state, converts this to a WorkflowState.
	 * @param stateString  The string defining the state.
	 * @return  A WorkflowState for the corresponding state.
	 */
	public static WorkflowState stringToState(String stateString) {
		WorkflowState state = stateMap.get(stateString);
		if (state == null) {
			state = UNKNOWN;
		}
		
		return state;
	}
	
	@Override
	public String toString() {
		return stateString;
	}
}
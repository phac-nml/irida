package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the state of a workflow.
 * Based off of states defined within Galaxy.
 * @see <a href="https://bitbucket.org/galaxy/galaxy-dist/src/7e257c7b10badb65772b1528cb61d58175a42e47/lib/galaxy/model/__init__.py?at=release_2014.06.02#cl-297">Galaxy Model</a>
 * @see <a href="https://bitbucket.org/galaxy/galaxy-dist/src/7e257c7b10badb65772b1528cb61d58175a42e47/lib/galaxy/webapps/galaxy/api/jobs.py?at=release_2014.06.02#cl-217">Galaxy API</a>
 * @see <a href="https://github.com/jmchilton/blend4j/blob/c5e3f157d402950a843d4e395e1daf889945d587/src/main/java/com/github/jmchilton/blend4j/galaxy/beans/HistoryDetails.java">HistoriDetails in blend4j</a>
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public enum WorkflowState {
	NEW("new"),
	UPLOAD("upload"),
	WAITING("waiting"),
	QUEUED("queued"),
	RUNNING("running"),
	
	/**
	 * The workflow will be in this state when everything is complete.
	 */
	OK("ok"),
	
	ERROR("error"),
	PAUSED("paused"),
	DELETED("deleted"),
	DELETED_NEW("deleted_new"),
	
	/**
	 * Unknown is used if the state from Galaxy is different from the above states (say if a new state was added to Galaxy).
	 */
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
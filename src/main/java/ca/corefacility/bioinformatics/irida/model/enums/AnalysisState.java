package ca.corefacility.bioinformatics.irida.model.enums;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Defins a set of states that an analysis submission can be in.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public enum AnalysisState {

	/**
	 * Occurs when an analysis is first submitted.
	 */
	SUBMITTED("SUBMITTED"),

	/**
	 * An analysis that is executing in the execution manager.
	 */
	RUNNING("RUNNING"),

	/**
	 * An analysis that has finished running in the execution manager.
	 */
	FINISHED_RUNNING("FINISHED_RUNNING"),

	/**
	 * An analysis that has completed and been loaded into IRIDA.
	 */
	COMPLETED("COMPLETED"),

	/**
	 * An analysis that was not successfully able to run.
	 */
	ERROR("ERROR");
	
	private static Map<String, AnalysisState> stateMap = new HashMap<>();
	private String stateString;
	
	/**
	 * Sets of a Map used to convert a string to a WorkflowState
	 */
	static {
		for (AnalysisState state : AnalysisState.values()) {
			stateMap.put(state.toString(), state);
		}
	}
	
	private AnalysisState(String stateString) {
		this.stateString = stateString;
	}
	
	/**
	 * Given a string defining a state, converts this to a AnalysisState.
	 * @param stateString  The string defining the state.
	 * @return  A AnalysisState for the corresponding state.
	 */
	public static AnalysisState fromString(String stateString) {
		AnalysisState state = stateMap.get(stateString);
		checkNotNull(state, "state for string \"" + stateString + "\" does not exist");
		
		return state;
	}
	
    @Override
    public String toString() {
        return stateString;
    }
}

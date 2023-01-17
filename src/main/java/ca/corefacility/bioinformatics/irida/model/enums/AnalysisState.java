package ca.corefacility.bioinformatics.irida.model.enums;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines a set of states for an {@link AnalysisSubmission}.
 */
public enum AnalysisState {

	/**
	 * Occurs when an analysis is first entered for submission.
	 */
	NEW("NEW"),

	/**
	 * Occurs when an analysis is downloading remote files
	 * 
	 * @deprecated This is no longer a valid state. This must still exist in IRIDA in cases where this state has been
	 *             recored in the audit tables.
	 */
	@Deprecated(since = "0.17.0")
	DOWNLOADING("DOWNLOADING"),

	/**
	 * Occurs when an analysis has completed downloading remote files
	 * 
	 * @deprecated This is no longer a valid state. This must still exist in IRIDA in cases where this state has been
	 *             recored in the audit tables.
	 */
	@Deprecated(since = "0.17.0")
	FINISHED_DOWNLOADING("FINISHED_DOWNLOADING"),

	/**
	 * Occurs when an analysis is starting to be submitted.
	 */
	PREPARING("PREPARING"),

	/**
	 * Occurs when an analysis is finished preparing.
	 */
	PREPARED("PREPARED"),

	/**
	 * Occurs when an analysis is first submitting.
	 */
	SUBMITTING("SUBMITTING"),

	/**
	 * An analysis that is running in the execution manager.
	 */
	RUNNING("RUNNING"),

	/**
	 * An analysis that has finished running in the execution manager.
	 */
	FINISHED_RUNNING("FINISHED_RUNNING"),

	/**
	 * An analysis that is complete but data needs to be transferred back into IRIDA.
	 */
	COMPLETING("COMPLETING"),

	/**
	 * An analysis that has completed and been loaded into IRIDA.
	 */
	COMPLETED("COMPLETED"),

	/**
	 * An analysis that has been transferred to IRIDA, but has not had post processing performed.
	 */
	TRANSFERRED("TRANSFERRED"),

	/**
	 * An analysis currently undergoing post processing
	 */
	POST_PROCESSING("POST_PROCESSING"),

	/**
	 * An analysis that was not successfully able to run.
	 */
	ERROR("ERROR");

	private static Map<String, AnalysisState> stateMap = new HashMap<>();
	private String stateString;

	/*
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
	 * 
	 * @param stateString The string defining the state.
	 * @return A AnalysisState for the corresponding state.
	 */
	public static AnalysisState fromString(String stateString) {
		AnalysisState state = stateMap.get(stateString);
		checkNotNull(state, "state for string \"" + stateString + "\" does not exist");

		return state;
	}

	/**
	 * Get the {@link AnalysisState}s that denote an {@link AnalysisSubmission} that has been picked up and is currently
	 * being processed.
	 * 
	 * @return a List of {@link AnalysisState}
	 */
	public static List<AnalysisState> getRunningStates() {
		return Lists.newArrayList(PREPARING, PREPARED, SUBMITTING, RUNNING, FINISHED_RUNNING, COMPLETING, TRANSFERRED,
				POST_PROCESSING);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return stateString;
	}
}

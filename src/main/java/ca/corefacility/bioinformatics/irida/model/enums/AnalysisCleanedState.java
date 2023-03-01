package ca.corefacility.bioinformatics.irida.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the state for cleaning up intermediate files of an analysis.
 */
public enum AnalysisCleanedState {

	/**
	 * A state where intermediate files exists for this analysis.
	 */
	NOT_CLEANED("NOT_CLEANED"),

	/**
	 * Indicates this analysis is in the process of being cleaned.
	 */
	CLEANING("CLEANING"),

	/**
	 * Set when an analysis is finished being cleaned.
	 */
	CLEANED("CLEANED"),

	/**
	 * Indicates that there was an error while cleaning a submission.
	 */
	CLEANING_ERROR("CLEANING_ERROR");

	private static Map<String, AnalysisCleanedState> stateMap = new HashMap<>();
	private String stateString;

	/*
	 * Sets of a Map used to convert a string to a analysis cleaned state.
	 */
	static {
		for (AnalysisCleanedState state : AnalysisCleanedState.values()) {
			stateMap.put(state.toString(), state);
		}
	}

	private AnalysisCleanedState(String stateString) {
		this.stateString = stateString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return stateString;
	}
}

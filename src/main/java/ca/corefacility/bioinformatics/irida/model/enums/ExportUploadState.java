package ca.corefacility.bioinformatics.irida.model.enums;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;

/**
 * Status of an {@link NcbiExportSubmission}
 */
public enum ExportUploadState {

	/**
	 * Newly created submission
	 */
	NEW("NEW"),

	/**
	 * Submission currently being processed and uploaded
	 */
	PROCESSING("PROCESSING"),

	/**
	 * Submission which has been successfully uploaded
	 */
	COMPLETE("COMPLETE"),

	/**
	 * Submission where an error occurred while processing
	 */
	ERROR("ERROR");

	private static Map<String, ExportUploadState> stateMap = new HashMap<>();
	private String stateString;

	static {
		for (ExportUploadState state : ExportUploadState.values()) {
			stateMap.put(state.toString(), state);
		}
	}

	private ExportUploadState(String stateString) {
		this.stateString = stateString;
	}

	/**
	 * Get an {@link ExportUploadState} from its string representation
	 * 
	 * @param stateString
	 *            The state as a String
	 * @return {@link ExportUploadState} for the given string
	 */
	public static ExportUploadState fromString(String stateString) {
		ExportUploadState state = stateMap.get(stateString);
		checkNotNull(state, "state for string \"" + stateString + "\" does not exist");

		return state;
	}

	/**
	 * Return the String representation of the {@link ExportUploadState}
	 */
	@Override
	public String toString() {
		return stateString;
	}
}

package ca.corefacility.bioinformatics.irida.model.enums;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

public enum ExportUploadState {

	NEW("NEW"), PROCESSING("PROCESSING"), COMPLETE("COMPLETE"), ERROR("ERROR");

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

	public static ExportUploadState fromString(String stateString) {
		ExportUploadState state = stateMap.get(stateString);
		checkNotNull(state, "state for string \"" + stateString + "\" does not exist");

		return state;
	}

	@Override
	public String toString() {
		return stateString;
	}
}

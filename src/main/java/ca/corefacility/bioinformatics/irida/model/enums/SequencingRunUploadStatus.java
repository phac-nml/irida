package ca.corefacility.bioinformatics.irida.model.enums;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;

/**
 * Encodes the status of an uploaded {@link SequencingRun}.
 * 
 * @see SequencingRun
 *
 */
public enum SequencingRunUploadStatus {
	UPLOADING("UPLOADING"), ERROR("ERROR"), COMPLETE("COMPLETE");

	private String code;

	private SequencingRunUploadStatus(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}

	public static SequencingRunUploadStatus fromString(String code) {
		switch (code.toUpperCase()) {
		case "UPLOADING":
			return UPLOADING;
		case "ERROR":
			return ERROR;
		case "COMPLETE":
			return COMPLETE;
		default:
			return ERROR;
		}
	}
}

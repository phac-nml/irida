package ca.corefacility.bioinformatics.irida.model.enums;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;

/**
 * Encodes the status of an uploaded resource.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @see SequencingRun
 *
 */
public enum UploadStatus {
	UPLOADING("UPLOADING"), ERROR("ERROR"), COMPLETE("COMPLETE");

	private String code;

	private UploadStatus(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}

	public static UploadStatus fromString(String code) {
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

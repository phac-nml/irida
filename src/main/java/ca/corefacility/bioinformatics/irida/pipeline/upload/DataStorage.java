package ca.corefacility.bioinformatics.irida.pipeline.upload;

/**
 * Defines the location of data to upload on a filesystem.
 * 
 * 
 */
public enum DataStorage {
	/**
	 * LOCAL implies the data to upload is on the same filesystem as the
	 * remote site (e.g. NFS shared filesystem).
	 */
	LOCAL,

	/**
	 * REMOTE implies the data to upload is on a separate filesystem as the
	 * remote site (e.g. no NFS shared filesystem, so requires uploading a
	 * copy of the files).
	 */
	REMOTE
}
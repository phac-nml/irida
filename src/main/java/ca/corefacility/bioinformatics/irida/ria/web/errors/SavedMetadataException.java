package ca.corefacility.bioinformatics.irida.ria.web.errors;

import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;

/**
 * Returns the SampleMetadataStorage on error.
 */
public class SavedMetadataException extends Exception {
	private SampleMetadataStorage storage;

	public SavedMetadataException(SampleMetadataStorage storage) {
		super();
		this.storage = storage;
	}

	public SampleMetadataStorage getStorage() {
		return storage;
	}
}

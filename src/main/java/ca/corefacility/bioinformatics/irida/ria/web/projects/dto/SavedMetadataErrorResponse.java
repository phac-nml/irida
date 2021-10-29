package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Returns the SampleMetadataStorage on error.
 */
public class SavedMetadataErrorResponse extends AjaxResponse {
	private SampleMetadataStorage storage;

	public SavedMetadataErrorResponse(SampleMetadataStorage storage) {
		this.storage = storage;
	}

	public SampleMetadataStorage getStorage() {
		return storage;
	}
}

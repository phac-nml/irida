package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Returns an ajax response with locked sample ids.
 */
public class LockedSamplesResponse extends AjaxResponse {
	private List<Long> sampleIds;

	public LockedSamplesResponse(List<Long> sampleIds) {
		this.sampleIds = sampleIds;
	}

	public List<Long> getSampleIds() {
		return sampleIds;
	}
}


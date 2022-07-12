package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Return a list of samples.
 */
public class SampleNameListResponse extends AjaxResponse {

	private List<SampleNameListItemModel> samples;

	public SampleNameListResponse(List<SampleNameListItemModel> samples) {
		this.samples = samples;
	}

	public List<SampleNameListItemModel> getSamples() {
		return samples;
	}

}

package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;

/**
 * Return a list of sample names.
 */
public class ValidateSampleNamesResponse extends AjaxResponse {
	private List<ValidateSampleNameModel> samples;

	public ValidateSampleNamesResponse(List<ValidateSampleNameModel> samples) {
		this.samples = samples;
	}

	public List<ValidateSampleNameModel> getSamples() {
		return samples;
	}
}

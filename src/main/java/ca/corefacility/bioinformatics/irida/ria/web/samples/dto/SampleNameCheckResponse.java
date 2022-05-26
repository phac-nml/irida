package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

/**
 * DTO for when checking a list of sample names against a project. Returns a list of valid samples formatted with
 * minimal details, and just the names of the ones that do not belong to the project.
 */
public class SampleNameCheckResponse {
	final List<ValidSample> valid;
	final List<String> invalid;

	public SampleNameCheckResponse(List<ValidSample> valid, List<String> invalid) {
		this.valid = valid;
		this.invalid = invalid;
	}

	public List<ValidSample> getValid() {
		return valid;
	}

	public List<String> getInvalid() {
		return invalid;
	}
}

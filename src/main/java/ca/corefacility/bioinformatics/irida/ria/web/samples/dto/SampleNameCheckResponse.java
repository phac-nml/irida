package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

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

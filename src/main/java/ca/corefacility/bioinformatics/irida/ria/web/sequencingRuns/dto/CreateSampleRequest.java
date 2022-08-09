package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto;

import java.util.List;

/**
 * UI request to update or create new samples.
 */
public class CreateSampleRequest {

	private List<SampleModel> samples;

	public CreateSampleRequest() {
	}

	public CreateSampleRequest(List<SampleModel> samples) {
		this.samples = samples;
	}

	public List<SampleModel> getSamples() {
		return samples;
	}

	public void setSamples(List<SampleModel> samples) {
		this.samples = samples;
	}
}

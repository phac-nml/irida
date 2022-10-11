package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

public class SampleConcatenationModel {
	private List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels;
	private String concatenationError;

	public SampleConcatenationModel(List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels) {
		this.sampleSequencingObjectFileModels = sampleSequencingObjectFileModels;
		this.concatenationError = null;
	}

	public SampleConcatenationModel(String concatenationError) {
		this.sampleSequencingObjectFileModels = null;
		this.concatenationError = concatenationError;
	}

	public List<SampleSequencingObjectFileModel> getSampleSequencingObjectFileModels() {
		return sampleSequencingObjectFileModels;
	}

	public String getConcatenationError() {
		return concatenationError;
	}
}

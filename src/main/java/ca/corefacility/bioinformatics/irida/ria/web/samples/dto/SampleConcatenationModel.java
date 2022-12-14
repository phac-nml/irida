package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import java.util.List;

/**
 * Used to send concatenated sequencing object information to the UI
 */

public class SampleConcatenationModel {
	private List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels;
	private String concatenationError;

	private String concatenationSuccess;

	public SampleConcatenationModel(List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels, String concatenationSuccess) {
		this.sampleSequencingObjectFileModels = sampleSequencingObjectFileModels;
		this.concatenationError = null;
		this.concatenationSuccess = concatenationSuccess;
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

	public String getConcatenationSuccess() {
		return concatenationSuccess;
	}

}

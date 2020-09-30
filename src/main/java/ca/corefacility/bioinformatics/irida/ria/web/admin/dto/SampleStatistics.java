package ca.corefacility.bioinformatics.irida.ria.web.admin.dto;

/**
 * Used by the UI to to get updated sample statistics.
 */

public class SampleStatistics {
	private Long numSamples;

	public SampleStatistics(Long numSamples) {
		this.numSamples = numSamples;
	}

	public Long getNumSamples() {
		return numSamples;
	}

	public void setNumSamples(Long numSamples) {
		this.numSamples = numSamples;
	}
}

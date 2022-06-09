package ca.corefacility.bioinformatics.irida.ria.web.models;

public enum ModelKeys {
	SequenceFileModel("sf-"),
	SingleEndSequenceFileModel("sesf-"),
	PairedEndSequenceFileModel("pesf-");

	public final String label;

	private ModelKeys(String label) {
		this.label = label;
	}
}
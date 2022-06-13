package ca.corefacility.bioinformatics.irida.ria.web.models;

/**
 * Use as prefixes when generating UI model keys.  This will add a unique prefix to your model.
 */
public enum ModelKeys {
	Project("proj-"),
	SequenceFileModel("sf-"),
	SingleEndSequenceFileModel("sesf-"),
	PairedEndSequenceFileModel("pesf-"),
	User("u-");

	public final String label;

	private ModelKeys(String label) {
		this.label = label;
	}
}
package ca.corefacility.bioinformatics.irida.ria.web.models;

/**
 * Use as prefixes when generating UI model keys.  This will add a unique prefix to your model.
 */
public enum ModelKeys {
	SequenceFileModel("sf-"),
	SingleEndSequenceFileModel("sesf-"),
	PairedEndSequenceFileModel("pesf-"),
	Project("proj-"),
	User("user-");

	public final String label;

	private ModelKeys(String label) {
		this.label = label;
	}
}
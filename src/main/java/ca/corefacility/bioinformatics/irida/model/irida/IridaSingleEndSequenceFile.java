package ca.corefacility.bioinformatics.irida.model.irida;

/**
 * Interface describing a sequence file with only {@link IridaSequenceFile}
 * associated (not paired).
 */
public interface IridaSingleEndSequenceFile {

	/**
	 * Get the sequence file object stored by this entity
	 * 
	 * @return a {@link IridaSequenceFile}
	 */
	public IridaSequenceFile getSequenceFile();
}

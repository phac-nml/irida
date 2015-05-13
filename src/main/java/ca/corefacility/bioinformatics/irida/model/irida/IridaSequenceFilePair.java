package ca.corefacility.bioinformatics.irida.model.irida;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Interface describing a pair object storing forward and reverse
 * {@link SequenceFile}s
 */
public interface IridaSequenceFilePair {

	/**
	 * Get the pair's identifier
	 * 
	 * @return Long id
	 */
	public Long getId();

	/**
	 * Get the forward orientied {@link SequenceFile}
	 * 
	 * @return Forward {@link SequenceFile}
	 */
	public IridaSequenceFile getForwardSequenceFile();

	/**
	 * Get the reverse oriented {@link SequenceFile}
	 * 
	 * @return reverse {@link SequenceFile}
	 */
	public IridaSequenceFile getReverseSequenceFile();

	/**
	 * Get a Set of the {@link SequenceFile}s for this pair
	 * 
	 * @return Set of {@link SequenceFile}s
	 */
	public Set<? extends IridaSequenceFile> getFiles();
}

package ca.corefacility.bioinformatics.irida.processing;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 * A collection of {@link FileProcessor} that are executed, in order, on a
 * specific {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface FileProcessingChain {
	/**
	 * Launch the chain of {@link FileProcessor} on the specific
	 * {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 *            the file to process.
	 * @return any {@link Exception} thrown during chain processing (in the same
	 *         order as {@link FileProcessor} returned by
	 *         {@link #getFileProcessors()}).
	 */
	public List<Exception> launchChain(SequenceFile sequenceFile);

	/**
	 * Get the collection of {@link FileProcessor} that this
	 * {@link FileProcessingChain} manages.
	 * 
	 * @return the {@link FileProcessor} collection managed by this
	 *         {@link FileProcessingChain}.
	 */
	public List<FileProcessor> getFileProcessors();

	/**
	 * Set whether or not the {@link FileProcessingChain} should fail on *all*
	 * exceptions.
	 * 
	 * @param fastFail
	 *            whether or not the chain should fail on exceptions.
	 */
	public void setFastFail(Boolean fastFail);
}

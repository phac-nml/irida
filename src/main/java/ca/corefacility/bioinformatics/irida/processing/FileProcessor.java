package ca.corefacility.bioinformatics.irida.processing;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Responsible for processing a {@link SequenceFile} after the file has been
 * uploaded to the server.
 * 
 * 
 */
public interface FileProcessor {

	/**
	 * Process the provided {@link SequenceFile}, then (optionally) proceed to
	 * the next processor in the chain. Any modifications to the
	 * {@link SequenceFile} made by the {@link FileProcessor} should be
	 * persisted by the {@link FileProcessor} itself -- the
	 * {@link FileProcessingChain} *does not* handle persistence.
	 * 
	 * @param sequencingObject
	 *            the {@link SequencingObject} to process
	 * @throws FileProcessorException
	 *             when processing fails.
	 */
	public void process(SequencingObject sequencingObject);

	/**
	 * If the {@link FileProcessor} throws a {@link FileProcessorException}, the
	 * {@link FileProcessor} should inform the caller whether or not proceeding
	 * with the remaining {@link FileProcessor} instances in the chain is safe.
	 * If the {@link FileProcessor} modifies the file, then proceeding with the
	 * remaining {@link FileProcessor} in the chain is not safe.
	 * 
	 * @return whether or not the {@link FileProcessor} modifies the
	 *         {@link SequenceFile}.
	 */
	public Boolean modifiesFile();

	/**
	 * This method asks the file processor whether it should act on this file.
	 * The processor may have some settings that would not run on certain files.
	 * 
	 * @param sequencingObjectId
	 *            the {@link SequencingObject} id to check
	 * @return true if the processor should act on the file
	 */
	public default boolean shouldProcessFile(Long sequencingObjectId) {
		return true;
	}
}

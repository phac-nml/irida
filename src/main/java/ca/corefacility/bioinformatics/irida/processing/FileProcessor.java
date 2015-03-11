package ca.corefacility.bioinformatics.irida.processing;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

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
	 * @param sequenceFileId
	 *            the id of the file to process.
	 * @throws FileProcessorException
	 *             when processing fails.
	 */
	public void process(Long sequenceFileId) throws FileProcessorException;

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
}

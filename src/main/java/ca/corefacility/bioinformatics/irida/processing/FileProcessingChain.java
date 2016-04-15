package ca.corefacility.bioinformatics.irida.processing;

import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.FileProcessorTimeoutException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * A collection of {@link FileProcessor} that are executed, in order, on a
 * specific {@link SequencingObject}.
 * 
 * 
 */
public interface FileProcessingChain {
	/**
	 * Launch the chain of {@link FileProcessor} on the specific
	 * {@link SequencingObject}.
	 * 
	 * @param sequencingObjectId
	 *            the id of the {@link SequencingObject} to process.
	 * @return any {@link Exception} thrown during chain processing (in the same
	 *         order as {@link FileProcessor} returned by
	 *         {@link #getFileProcessors()}).
	 * @throws FileProcessorTimeoutException
	 *             when the processor chain waits too long for the specified
	 *             {@link SequencingObject} to appear in the database.
	 */
	public List<Exception> launchChain(Long sequencingObjectId) throws FileProcessorTimeoutException;

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

	/**
	 * Set the total amount of time (in seconds) that the processor chain should
	 * wait for the {@link SequencingObject} to appear before failing.
	 * 
	 * @param timeout
	 *            the total amount of time in seconds that the processor chain
	 *            should wait.
	 */
	public void setTimeout(Integer timeout);

	/**
	 * Set the amount of time (in seconds) that the processor chain should sleep
	 * each time it sleeps.
	 * 
	 * @param sleepDuration
	 *            the amount of time in seconds that the processor chain should
	 *            wait.
	 */
	public void setSleepDuration(Integer sleepDuration);
}

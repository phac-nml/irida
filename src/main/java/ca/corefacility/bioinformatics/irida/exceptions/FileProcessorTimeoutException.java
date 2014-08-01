package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;

/**
 * Thrown when the {@link FileProcessingChain} waits longer than a specified
 * timeout for the {@link SequenceFileService} to finish executing its
 * transaction.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class FileProcessorTimeoutException extends Exception {
	public FileProcessorTimeoutException(String message) {
		super(message);
	}
}

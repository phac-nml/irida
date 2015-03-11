package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

/**
 * Thrown when the {@link FileProcessingChain} waits longer than a specified
 * timeout for the {@link SequenceFileService} to finish executing its
 * transaction.
 * 
 *
 */
public class FileProcessorTimeoutException extends Exception {

	private static final long serialVersionUID = -5547088975269003017L;

	public FileProcessorTimeoutException(String message) {
		super(message);
	}
}

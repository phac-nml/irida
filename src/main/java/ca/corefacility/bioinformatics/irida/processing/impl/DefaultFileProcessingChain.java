package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.FileProcessorTimeoutException;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

/**
 * Default implementation of {@link FileProcessingChain}. Simply iterates
 * through a collection of {@link FileProcessor}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class DefaultFileProcessingChain implements FileProcessingChain {

	private static final Logger logger = LoggerFactory.getLogger(DefaultFileProcessingChain.class);

	private final List<FileProcessor> fileProcessors;

	private Boolean fastFail = false;

	private Integer timeout = 60;
	
	private Integer sleepDuration = 1000;

	private final SequenceFileRepository sequenceFileRepository;

	public DefaultFileProcessingChain(SequenceFileRepository sequenceFileRepository, FileProcessor... fileProcessors) {
		this(sequenceFileRepository, Arrays.asList(fileProcessors));
	}

	public DefaultFileProcessingChain(SequenceFileRepository sequenceFileRepository, List<FileProcessor> fileProcessors) {
		this.fileProcessors = fileProcessors;
		this.sequenceFileRepository = sequenceFileRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Exception> launchChain(Long sequenceFileId) throws FileProcessorTimeoutException {
		List<Exception> ignoredExceptions = new ArrayList<>();
		Integer waiting = 0;

		// this class is typically launched in a thread after a file has been
		// initially saved to the database, but not necessarily before the
		// transaction has completed and closed, so we need to block until the
		// file has been persisted in the database.
		while (!sequenceFileRepository.exists(sequenceFileId)) {
			if (waiting > timeout) {
				throw new FileProcessorTimeoutException("Waiting for longer than " + sleepDuration * timeout + "ms, bailing out.");
			}
			
			waiting++;
			
			try {
				Thread.sleep(sleepDuration);
			} catch (InterruptedException e) {
			}
		}
		
		for (FileProcessor fileProcessor : fileProcessors) {
			try {
				fileProcessor.process(sequenceFileId);
			} catch (FileProcessorException e) {
				// if the file processor modifies the file, then just fast fail,
				// we can't proceed with the remaining file processors. If the
				// file processor *doesn't* modify the file, then continue with
				// execution (show the error, but proceed).
				if (fileProcessor.modifiesFile() || fastFail) {
					throw e;
				} else {
					ignoredExceptions.add(e);
					logger.error("File processor [" + fileProcessor.getClass() + "] failed to process [" + sequenceFileId
							+ "], but proceeding with the remaining processors because the "
							+ "file would not be modified by the processor. Stack trace follows.", e);
				}
			}
		}

		return ignoredExceptions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FileProcessor> getFileProcessors() {
		return fileProcessors;
	}

	public void setFastFail(Boolean fastFail) {
		this.fastFail = fastFail;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSleepDuration(Integer sleepDuration) {
		this.sleepDuration = sleepDuration * 1000;
	}
}

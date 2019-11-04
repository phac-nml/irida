package ca.corefacility.bioinformatics.irida.processing.impl;

import java.nio.file.Files;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.FileProcessorTimeoutException;
import ca.corefacility.bioinformatics.irida.model.sample.FileProcessorErrorQCEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

/**
 * Default implementation of {@link FileProcessingChain}. Simply iterates
 * through a collection of {@link FileProcessor}.
 * 
 * 
 */
public class DefaultFileProcessingChain implements FileProcessingChain {

	private static final Logger logger = LoggerFactory.getLogger(DefaultFileProcessingChain.class);

	private final List<FileProcessor> fileProcessors;

	private Boolean fastFail = false;

	private Integer timeout = 60;

	private Integer sleepDuration = 1000;

	private final SequencingObjectRepository sequencingObjectRepository;
	private QCEntryRepository qcRepository;

	public DefaultFileProcessingChain(SequencingObjectRepository sequencingObjectRepository,
			QCEntryRepository qcRepository, FileProcessor... fileProcessors) {
		this(sequencingObjectRepository, qcRepository, Arrays.asList(fileProcessors));
	}

	public DefaultFileProcessingChain(SequencingObjectRepository sequencingObjectRepository,
			QCEntryRepository qcRepository, List<FileProcessor> fileProcessors) {
		this.fileProcessors = fileProcessors;
		this.sequencingObjectRepository = sequencingObjectRepository;
		this.qcRepository = qcRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Exception> launchChain(Long sequencingObjectId) throws FileProcessorTimeoutException {
		List<Exception> ignoredExceptions = new ArrayList<>();
		Integer waiting = 0;

		// this class is typically launched in a thread after a file has been
		// initially saved to the database, but not necessarily before the
		// transaction has completed and closed, so we need to block until the
		// file has been persisted in the database.
		while (!sequencingObjectRepository.existsById(sequencingObjectId)) {
			if (waiting > timeout) {
				throw new FileProcessorTimeoutException(
						"Waiting for longer than " + sleepDuration * timeout + "ms, bailing out.");
			}

			waiting++;

			try {
				Thread.sleep(sleepDuration);
			} catch (InterruptedException e) {
			}
		}

		for (FileProcessor fileProcessor : fileProcessors) {
			try {
				if (fileProcessor.shouldProcessFile(sequencingObjectId)) {
					SequencingObject settledSequencingObject = getSettledSequencingObject(sequencingObjectId);

					fileProcessor.process(settledSequencingObject);
				}
			} catch (FileProcessorException e) {
				SequencingObject sequencingObject = sequencingObjectRepository.findById(sequencingObjectId).orElse(null);

				qcRepository.save(new FileProcessorErrorQCEntry(sequencingObject));

				// if the file processor modifies the file, then just fast fail,
				// we can't proceed with the remaining file processors. If the
				// file processor *doesn't* modify the file, then continue with
				// execution (show the error, but proceed).
				if (fileProcessor.modifiesFile() || fastFail) {
					sequencingObject.setProcessingState(SequencingObject.ProcessingState.ERROR);
					sequencingObjectRepository.save(sequencingObject);

					throw e;
				} else {
					ignoredExceptions.add(e);
					logger.error("File processor [" + fileProcessor.getClass() + "] failed to process ["
							+ sequencingObjectId + "], but proceeding with the remaining processors because the "
							+ "file would not be modified by the processor. Stack trace follows.", e);
				}
			}
		}

		SequencingObject statusObject = sequencingObjectRepository.findById(sequencingObjectId).orElse(null);

		statusObject.setProcessingState(SequencingObject.ProcessingState.FINISHED);
		sequencingObjectRepository.save(statusObject);

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

	/**
	 * Checks the {@link SequenceFile}s for the given {@link SequencingObject}
	 * to see if it's files are in the place they should be. Since there's lots
	 * of saves going on during the {@link FileProcessingChain} the transaction
	 * might not be complete in the time the file is first read.
	 * 
	 * @param sequencingObjectId
	 *            the id of the {@link SequencingObject} to check
	 * @return the settled {@link SequencingObject}
	 * @throws FileProcessorTimeoutException
	 *             if the files don't settle in the configured timeout
	 */
	private SequencingObject getSettledSequencingObject(Long sequencingObjectId) throws FileProcessorTimeoutException {
		boolean filesNotSettled = true;

		Integer waiting = 0;

		Optional<SequencingObject> sequencingObject;

		do {
			if (waiting > timeout) {
				throw new FileProcessorTimeoutException("Waiting for longer than " + sleepDuration * timeout
						+ "ms, bailing out.  File id " + sequencingObjectId);
			}

			waiting++;

			try {
				Thread.sleep(sleepDuration);
			} catch (InterruptedException e) {
			}

			sequencingObject = sequencingObjectRepository.findById(sequencingObjectId);

			if(sequencingObject.isPresent()) {
				Set<SequenceFile> files = sequencingObject.get().getFiles();
				filesNotSettled = files.stream().anyMatch(f -> {
					return !Files.exists(f.getFile());
				});
			}
		} while (filesNotSettled);

		return sequencingObject.get();
	}
}

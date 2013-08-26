package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

/**
 * Default implementation of {@link FileProcessingChain}. Simply iterates
 * through a collection of {@link FileProcessor}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class DefaultFileProcessingChain implements FileProcessingChain {

	private static final Logger logger = LoggerFactory.getLogger(DefaultFileProcessingChain.class);

	private List<FileProcessor> fileProcessors;

	private Boolean fastFail = false;

	public DefaultFileProcessingChain(FileProcessor... fileProcessors) {
		this(Arrays.asList(fileProcessors));
	}

	public DefaultFileProcessingChain(List<FileProcessor> fileProcessors) {
		this.fileProcessors = fileProcessors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public List<Exception> launchChain(SequenceFile sequenceFile) {
		List<Exception> ignoredExceptions = new ArrayList<>();

		for (FileProcessor fileProcessor : fileProcessors) {
			try {
				sequenceFile = fileProcessor.process(sequenceFile);
			} catch (FileProcessorException e) {
				// if the file processor modifies the file, then just fast fail,
				// we can't proceed with the remaining file processors. If the
				// file processor *doesn't* modify the file, then continue with
				// execution (show the error, but proceed).
				if (fileProcessor.modifiesFile() || fastFail) {
					throw e;
				} else {
					ignoredExceptions.add(e);
					logger.error("File processor [" + fileProcessor.getClass() + "] failed to process [" + sequenceFile
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

}

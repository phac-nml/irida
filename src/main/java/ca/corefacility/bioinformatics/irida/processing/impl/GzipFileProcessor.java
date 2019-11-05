package ca.corefacility.bioinformatics.irida.processing.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.util.FileUtils;

/**
 * Handle gzip-ed files (if necessary). This class partially assumes that gzip
 * compressed files have the extension ".gz" (not for determining whether or not
 * the file is compressed, but rather for naming the decompressed file). If the
 * compressed file does not end with ".gz", then it will be renamed as such so
 * that the decompressed file name will not conflict with the compressed file
 * name.
 * 
 * 
 */
@Component
public class GzipFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(GzipFileProcessor.class);
	private static final String GZIP_EXTENSION = ".gz";

	private final SequenceFileRepository sequenceFileRepository;
	private boolean disableFileProcessor = false;
	private boolean removeCompressedFile;

	@Autowired
	public GzipFileProcessor(final SequenceFileRepository sequenceFileRepository) {
		this.sequenceFileRepository = sequenceFileRepository;
		removeCompressedFile = false;
	}

	public GzipFileProcessor(final SequenceFileRepository sequenceFileRepository, Boolean removeCompressedFiles) {
		this.sequenceFileRepository = sequenceFileRepository;
		this.removeCompressedFile = removeCompressedFiles;
	}

	/**
	 * Decide whether or not to delete the original compressed files that are
	 * uploaded once they're unzipped. If <code>false</code> they will be kept
	 * in their revision directories.
	 * 
	 * @param removeCompressedFile
	 *            Whether or not to delete original compressed files.
	 */
	public void setRemoveCompressedFiles(boolean removeCompressedFile) {
		this.removeCompressedFile = removeCompressedFile;
	}
	
	/**
	 * Disables this file processor from processing files.
	 * 
	 * @param disableFileProcessor True if this processor should be disabled, false
	 *                             otherwise.
	 */
	public void setDisableFileProcessor(boolean disableFileProcessor) {
		this.disableFileProcessor = disableFileProcessor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public void process(SequencingObject sequencingObject) {
		if (!disableFileProcessor) {
			for (SequenceFile file : sequencingObject.getFiles()) {
				processSingleFile(file);
			}
		} else {
			logger.debug("Not running process. It has been disabled");
		}
	}

	/**
	 * Process a single {@link SequenceFile}
	 * 
	 * @param sequenceFile
	 *            file to process
	 * @throws FileProcessorException
	 *             if an error occurs while processing
	 */
	public void processSingleFile(SequenceFile sequenceFile) throws FileProcessorException {
		if (disableFileProcessor) {
			logger.debug("Not running processSingleFile. It has been disabled");
			return;
		}
		
		Path file = sequenceFile.getFile();
		String nameWithoutExtension = file.getFileName().toString();

		// strip the extension from the filename (if necessary)
		if (nameWithoutExtension.endsWith(GZIP_EXTENSION)) {
			nameWithoutExtension = nameWithoutExtension.substring(0, nameWithoutExtension.lastIndexOf(GZIP_EXTENSION));
		}

		try {
			logger.trace("About to try handling a gzip file.");
			if (FileUtils.isGzipped(file)) {
				file = addExtensionToFilename(file, GZIP_EXTENSION);

				try (GZIPInputStream zippedInputStream = new GZIPInputStream(Files.newInputStream(file))) {
					logger.trace("Handling gzip compressed file.");

					Path targetDirectory = Files.createTempDirectory(null);
					Path target = targetDirectory.resolve(nameWithoutExtension);
					logger.debug("Target directory is [" + targetDirectory + "]");
					logger.debug("Writing uncompressed file to [" + target + "]");

					Files.copy(zippedInputStream, target);

					sequenceFile.setFile(target);
					sequenceFile = sequenceFileRepository.save(sequenceFile);

					if (removeCompressedFile) {
						logger.debug(
								"Removing original compressed files [file.processing.decompress.remove.compressed.file=true]");
						try {
							Files.delete(file);
						} catch (final Exception e) {
							logger.error("Failed to remove the original compressed file.", e);
							// throw the exception again to be caught by the
							// outer try/catch block:
							throw e;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Failed to process the input file [" + sequenceFile + "]; stack trace follows.", e);
			throw new FileProcessorException("Failed to process input file [" + sequenceFile + "].");
		}
	}

	/**
	 * Ensures that the supplied file ends with a specific extension.
	 * 
	 * @param file
	 *            the file to handle.
	 * @return the modified (or not) file.
	 */
	private Path addExtensionToFilename(Path file, String extension) throws IOException {
		String currentName = file.toString();
		if (!currentName.endsWith(extension)) {
			String modifiedName = new StringBuilder(currentName).append(extension).toString();
			Path target = Paths.get(modifiedName);
			file = Files.move(file, target);
		}

		return file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return !disableFileProcessor;
	}
}

package ca.corefacility.bioinformatics.irida.processing.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

/**
 * Handle gzip-ed files (if necessary). This class partially assumes that gzip
 * compressed files have the extension ".gz" (not for determining whether or not
 * the file is compressed, but rather for naming the decompressed file). If the
 * compressed file does not end with ".gz", then it will be renamed as such so
 * that the decompressed file name will not conflict with the compressed file
 * name.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class GzipFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(GzipFileProcessor.class);
	private static final String GZIP_EXTENSION = ".gz";

	private final SequenceFileRepository sequenceFileRepository;
	private final Boolean removeCompressedFile;

	public GzipFileProcessor(final SequenceFileRepository sequenceFileService, final Boolean removeCompressedFile) {
		this.sequenceFileRepository = sequenceFileService;
		this.removeCompressedFile = removeCompressedFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void process(Long sequenceFileId) throws FileProcessorException {
		SequenceFile sequenceFile = sequenceFileRepository.findOne(sequenceFileId);
		Path file = sequenceFile.getFile();
		String nameWithoutExtension = file.getFileName().toString();

		// strip the extension from the filename (if necessary)
		if (nameWithoutExtension.endsWith(GZIP_EXTENSION)) {
			nameWithoutExtension = nameWithoutExtension.substring(0, nameWithoutExtension.lastIndexOf(GZIP_EXTENSION));
		}

		try {
			logger.trace("About to try handling a gzip file.");
			if (isCompressed(file)) {
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
						logger.debug("Removing original compressed files [file.processing.decompress.remove.compressed.file=true]");
						try {
							Files.delete(file);
						} catch (final Exception e) {
							logger.error("Failed to remove the original compressed file.", e);
							// throw the exception again to be caught by the outer try/catch block:
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
		return true;
	}

	/*
	 * Determines if a byte array is compressed. Adapted from stackoverflow
	 * answer:
	 * 
	 * @see
	 * http://stackoverflow.com/questions/4818468/how-to-check-if-inputstream
	 * -is-gzipped#answer-8620778
	 * 
	 * @param bytes an array of bytes
	 * 
	 * @return true if the array is compressed or false otherwise
	 * 
	 * @throws java.io.IOException if the byte array couldn't be read
	 */
	private boolean isCompressed(Path file) throws IOException {
		try (InputStream is = Files.newInputStream(file, StandardOpenOption.READ)) {
			byte[] bytes = new byte[2];
			is.read(bytes);
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
		}
	}
}

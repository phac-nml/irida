package ca.corefacility.bioinformatics.irida.processing.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

/**
 * {@link FileProcessor} used to calculate a checksum using sha256 for uploaded
 * {@link SequenceFile}s
 */
@Component
public class ChecksumFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(ChecksumFileProcessor.class);

	private SequenceFileRepository fileRepository;
	private IridaFileStorageUtility iridaFileStorageUtility;

	@Autowired
	public ChecksumFileProcessor(SequenceFileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}

	public ChecksumFileProcessor(SequenceFileRepository fileRepository, IridaFileStorageUtility iridaFileStorageUtility) {
		this.fileRepository = fileRepository;
		this.iridaFileStorageUtility = iridaFileStorageUtility;
	}

	/**
	 * Create an sha256sum for the files in a {@link SequencingObject} and save
	 * it with the file.
	 * 
	 * @param sequencingObject
	 *            the {@link SequencingObject} to modify
	 * @throws FileProcessorException
	 *             a {@link FileProcessorException} if the file could not be
	 *             processed
	 */
	@Override
	public void process(SequencingObject sequencingObject) {
		Set<SequenceFile> files = sequencingObject.getFiles();

		for (SequenceFile file : files) {

			try (InputStream is = file.getFileInputStream()) {
				String shaDigest = DigestUtils.sha256Hex(is);
				logger.trace("Checksum generated for file " + file.getId() + ": " + shaDigest);
				file.setUploadSha256(shaDigest);
				fileRepository.saveMetadata(file);
			} catch (IOException | StorageException e) {
				throw new FileProcessorException("could not calculate checksum", e);
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

}

package ca.corefacility.bioinformatics.irida.processing.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

/**
 * {@link FileProcessor} used to calculate a checksum using sha256 for uploaded
 * {@link SequenceFile}s
 */
@Component
public class ChecksumFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(ChecksumFileProcessor.class);

	private SequenceFileRepository fileRepository;

	private SequencingObjectRepository objectRepository;

	@Autowired
	public ChecksumFileProcessor(SequencingObjectRepository objectRepository, SequenceFileRepository fileRepository) {
		this.objectRepository = objectRepository;
		this.fileRepository = fileRepository;
	}

	/**
	 * Create an sha256sum for the files in a {@link SequencingObject} and save it
	 * with the file.
	 * 
	 * @param sequenceFileId
	 *            the id of the {@link SequencingObject} to modify
	 * @throws FileProcessorException
	 *             a {@link FileProcessorException} if the file could not be
	 *             processed
	 */
	@Override
	public void process(Long sequenceFileId) throws FileProcessorException {
		SequencingObject sequencingObject = objectRepository.findOne(sequenceFileId);
		Set<SequenceFile> files = sequencingObject.getFiles();

		for (SequenceFile file : files) {

			try (InputStream is = Files.newInputStream(file.getFile())) {
				String shaDigest = DigestUtils.sha256Hex(is);
				logger.trace("Checksum generated for file " + file.getId() + ": " + shaDigest);
				file.setUploadSha256(shaDigest);

				fileRepository.save(file);
			} catch (IOException e) {
				throw new FileProcessorException("could not calculate checksum", e);
			}

		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

}

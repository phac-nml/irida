package ca.corefacility.bioinformatics.irida.processing.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

@Component
public class ChecksumFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(ChecksumFileProcessor.class);

	@Autowired
	private SequenceFileRepository fileRepository;
	@Autowired
	private SequencingObjectRepository objectRepository;

	@Override
	public void process(Long sequenceFileId) throws FileProcessorException {
		SequencingObject sequencingObject = objectRepository.findOne(sequenceFileId);
		Set<SequenceFile> files = sequencingObject.getFiles();

		for (SequenceFile file : files) {

			try (InputStream is = Files.newInputStream(file.getFile())) {
				String md5Digest = DigestUtils.md5DigestAsHex(is);
				logger.trace("Checksum generated for file " + file.getId() + ": " + md5Digest);
				file.setChecksum(md5Digest);

				fileRepository.save(file);
			} catch (IOException e) {
				logger.error("Could not calculate checksum", e);
			}

		}

	}

	@Override
	public Boolean modifiesFile() {
		return false;
	}

}

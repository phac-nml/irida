package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;

/**
 * Custom implementation of {@link SequenceFileRepository} that writes the
 * {@link Path} part of a {@link SequenceFile} to disk.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class SequenceFileRepositoryImpl implements SequenceFileRepositoryCustom {

	private static final Logger logger = LoggerFactory.getLogger(SequenceFileRepositoryImpl.class);
	private final Path baseDirectory;
	private final EntityManager entityManager;

	@Autowired
	public SequenceFileRepositoryImpl(final EntityManager entityManager, final Path baseDirectory) {
		this.entityManager = entityManager;
		this.baseDirectory = baseDirectory;
	}

	public SequenceFile save(final SequenceFile sequenceFile) {
		if (sequenceFile.getId() == null) {
			// save the initial version of the file to the database so that we
			// get an identifier attached to it.
			entityManager.persist(sequenceFile);
		}
		sequenceFile.setFileRevisionNumber(sequenceFile.getFileRevisionNumber() + 1);
		writeSequenceFileToDisk(sequenceFile);
		entityManager.persist(sequenceFile);
		// entityManager.flush();

		return sequenceFile;
	}

	/**
	 * Write the {@link Path} part of the {@link SequenceFile} to disk instead
	 * of storing it in the database.
	 * 
	 * @param sequenceFile
	 *            the {@link SequenceFile} to write to disk.
	 * @return the {@link SequenceFile} with an updated path indicating where
	 *         the file was saved.
	 * @throws IllegalArgumentException
	 *             if the {@link SequenceFile} provided has not previously been
	 *             persisted.
	 */
	private SequenceFile writeSequenceFileToDisk(SequenceFile sequenceFile) throws IllegalArgumentException {
		if (sequenceFile.getId() == null) {
			throw new IllegalArgumentException("Identifier is required.");
		}
		Path sequenceFileDir = getSequenceFileDir(sequenceFile.getId());
		Path sequenceFileDirWithRevision = getSequenceFileDirWithRevision(sequenceFileDir,
				sequenceFile.getFileRevisionNumber());
		Path target = sequenceFileDirWithRevision.resolve(sequenceFile.getFile().getFileName());
		try {
			if (!Files.exists(sequenceFileDir)) {
				Files.createDirectory(sequenceFileDir);
				logger.debug("Created directory: [" + sequenceFileDir.toString() + "]");
			}

			if (!Files.exists(sequenceFileDirWithRevision)) {
				Files.createDirectory(sequenceFileDirWithRevision);
				logger.debug("Created directory: [" + sequenceFileDirWithRevision.toString() + "]");
			}

			SequenceFile inDatabase = entityManager.find(SequenceFile.class, sequenceFile.getId());
			logger.debug("Going to move file with id [" + sequenceFile.getId() + "] from [" + sequenceFile.getFile()
					+ "] to [" + target + "]; the current entry in the database is stored at location ["
					+ inDatabase.getFile() + "];");

			Files.move(sequenceFile.getFile(), target);
			logger.debug("Moved file " + sequenceFile.getFile() + " to " + target);
		} catch (IOException e) {
			e.printStackTrace();
			throw new StorageException("Failed to move file into new directory.");
		}
		sequenceFile.setFile(target);
		return sequenceFile;
	}

	/**
	 * Get the appropriate directory for the {@link SequenceFile}.
	 * 
	 * @param id
	 *            the identifier of the {@link SequenceFile}.
	 * @return the {@link Path} for the {@link SequenceFile}.
	 */
	private Path getSequenceFileDir(Long id) {
		return baseDirectory.resolve(id.toString());
	}

	/**
	 * Get sequence file directory including revision number
	 * 
	 * @param sequenceBaseDir
	 *            The sequence file's base directory
	 * @param fileRevisionNumber
	 *            The revision number for this file
	 * @return The directory to write the file revision
	 */
	private Path getSequenceFileDirWithRevision(Path sequenceBaseDir, Long fileRevisionNumber) {
		return sequenceBaseDir.resolve(fileRevisionNumber.toString());
	}

}

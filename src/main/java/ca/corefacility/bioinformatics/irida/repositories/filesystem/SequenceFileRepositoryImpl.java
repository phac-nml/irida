package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.nio.file.Path;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;

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
		PathRepository.writeFilesToDisk(baseDirectory, sequenceFile);
		return entityManager.merge(sequenceFile);
	}
}

package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.nio.file.Path;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

/**
 * Custom implementation of {@link FilesystemSupplementedRepositoryImpl} for {@link SequenceFile}.
 */
@Repository
public class SequenceFileRepositoryImpl extends FilesystemSupplementedRepositoryImpl<SequenceFile> {

	@Autowired
	public SequenceFileRepositoryImpl(EntityManager entityManager,
			@Qualifier("sequenceFileBaseDirectory") Path baseDirectory,
			IridaFileStorageUtility iridaFileStorageUtility) {
		super(entityManager, baseDirectory, iridaFileStorageUtility);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public SequenceFile save(SequenceFile entity) {
		return super.saveInternal(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(SequenceFile entity) {
		super.deleteInternal(entity);
	}

}

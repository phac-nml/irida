package ca.corefacility.bioinformatics.irida.repositories.referencefile;

import java.nio.file.Path;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.service.util.SequenceFileUtilities;

/**
 * Custom implementation of {@link FilesystemSupplementedRepositoryImpl} for {@link ReferenceFile}.
 */
@Repository
public class ReferenceFileRepositoryImpl extends FilesystemSupplementedRepositoryImpl<ReferenceFile> {

	private final SequenceFileUtilities sequenceFileUtilities;

	@Autowired
	public ReferenceFileRepositoryImpl(final EntityManager entityManager,
			final SequenceFileUtilities sequenceFileUtilities,
			final @Qualifier("referenceFileBaseDirectory") Path baseDirectory,
			IridaFileStorageUtility iridaFileStorageUtility) {
		super(entityManager, baseDirectory, iridaFileStorageUtility);
		this.sequenceFileUtilities = sequenceFileUtilities;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public ReferenceFile save(ReferenceFile entity) {
		final Long referenceFileLength = sequenceFileUtilities.countSequenceFileLengthInBases(entity.getFile());
		entity.setFileLength(referenceFileLength);
		return super.saveInternal(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(ReferenceFile entity) {
		super.deleteInternal(entity);
	}

}

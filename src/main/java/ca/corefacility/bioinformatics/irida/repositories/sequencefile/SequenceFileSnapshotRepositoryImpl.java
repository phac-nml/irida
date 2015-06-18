package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.nio.file.Path;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;

public class SequenceFileSnapshotRepositoryImpl extends FilesystemSupplementedRepositoryImpl<SequenceFileSnapshot> {

	@Autowired
	public SequenceFileSnapshotRepositoryImpl(EntityManager entityManager,
			@Qualifier("snapshotFileBaseDirectory") Path baseDirectory) {
		super(entityManager, baseDirectory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFileSnapshot save(SequenceFileSnapshot entity) {
		return super.saveInternal(entity);
	}

}

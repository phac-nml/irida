package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.nio.file.Path;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;

public class RemoteSequenceFileRepositoryImpl extends FilesystemSupplementedRepositoryImpl<RemoteSequenceFile> {

	@Autowired
	public RemoteSequenceFileRepositoryImpl(EntityManager entityManager,
			@Qualifier("sequenceFileBaseDirectory") Path baseDirectory) {
		super(entityManager, baseDirectory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RemoteSequenceFile save(RemoteSequenceFile entity) {
		return super.saveInternal(entity);
	}

}

package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.nio.file.Path;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;

/**
 * Custom implementation of {@link FilesystemSupplementedRepositoryImpl} for
 * {@link SequenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Repository
public class SequenceFileRepositoryImpl extends FilesystemSupplementedRepositoryImpl<SequenceFile> {

	@Autowired
	public SequenceFileRepositoryImpl(EntityManager entityManager,
			@Qualifier("sequenceFileBaseDirectory") Path baseDirectory) {
		super(entityManager, baseDirectory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFile save(SequenceFile entity) {
		return super.saveInternal(entity);
	}

}

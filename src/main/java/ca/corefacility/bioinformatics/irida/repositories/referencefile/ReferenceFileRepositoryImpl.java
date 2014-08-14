package ca.corefacility.bioinformatics.irida.repositories.referencefile;

import java.nio.file.Path;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;

/**
 * Custom implementation of {@link FilesystemSupplementedRepositoryImpl} for
 * {@link ReferenceFile}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Repository
public class ReferenceFileRepositoryImpl extends FilesystemSupplementedRepositoryImpl<ReferenceFile> {

	@Autowired
	public ReferenceFileRepositoryImpl(EntityManager entityManager,
			@Qualifier("referenceFileBaseDirectory") Path baseDirectory) {
		super(entityManager, baseDirectory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReferenceFile save(ReferenceFile entity) {
		return super.saveInternal(entity);
	}

}

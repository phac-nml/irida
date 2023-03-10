package ca.corefacility.bioinformatics.irida.repositories.assembly;

import java.nio.file.Path;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

/**
 * A {@link FilesystemSupplementedRepositoryImpl} implementation for {@link GenomeAssembly}
 */
@Repository
public class GenomeAssemblyRepositoryImpl extends FilesystemSupplementedRepositoryImpl<GenomeAssembly> {

	@Autowired
	public GenomeAssemblyRepositoryImpl(EntityManager entityManager,
			@Qualifier("assemblyFileBaseDirectory") Path baseDirectory,
			IridaFileStorageUtility iridaFileStorageUtility) {
		super(entityManager, baseDirectory, iridaFileStorageUtility);
	}

	@Override
	public GenomeAssembly save(GenomeAssembly entity) {
		if (entity instanceof UploadedAssembly) {
			return this.saveInternal(entity);
		} else {
			return this.save(entity);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(GenomeAssembly entity) {
		this.deleteInternal(entity);
	}
}

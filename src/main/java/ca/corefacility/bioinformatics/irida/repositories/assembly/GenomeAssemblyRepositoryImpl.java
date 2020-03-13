package ca.corefacility.bioinformatics.irida.repositories.assembly;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.EntityManager;
import java.nio.file.Path;

public class GenomeAssemblyRepositoryImpl extends FilesystemSupplementedRepositoryImpl<GenomeAssembly> {

	@Autowired
	public GenomeAssemblyRepositoryImpl(EntityManager entityManager,
			@Qualifier("assemblyBaseDirectory") Path baseDirectory) {
		super(entityManager, baseDirectory);
	}

	@Override
	public GenomeAssembly save(GenomeAssembly entity) {
		if (entity instanceof UploadedAssembly) {
			return this.saveInternal(entity);
		} else {
			return this.save(entity);
		}

	}
}

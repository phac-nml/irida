package ca.corefacility.bioinformatics.irida.repositories.assembly;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

public interface GenomeAssemblyRepository
		extends FilesystemSupplementedRepository<UploadedAssembly>, IridaJpaRepository<GenomeAssembly, Long> {

	@Override
	<S extends GenomeAssembly> S save(S entity);
}

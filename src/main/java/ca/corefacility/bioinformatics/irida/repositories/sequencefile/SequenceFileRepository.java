package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;

/**
 * A repository to store information about sequence files. This repository will
 * not directly store the file, just metadata
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SequenceFileRepository extends FilesystemSupplementedRepository<SequenceFile>,
		IridaJpaRepository<SequenceFile, Long> {
	/**
	 * Get the collection of {@link SequenceFile} created as part of a
	 * {@link SequencingRun}.
	 * 
	 * @param sequencingRun
	 *            the run to load the files for.
	 * @return the files created as part of a run.
	 */
	@Query("select f from SequenceFile f where f.sequencingRun = ?1")
	public Set<SequenceFile> findSequenceFilesForSequencingRun(SequencingRun sequencingRun);
}

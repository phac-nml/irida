package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;

/**
 * A repository to store information about sequence files. This repository will
 * not directly store the file, just metadata
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */

public interface SequenceFileRepository extends PagingAndSortingRepository<SequenceFile, Long>,
		RevisionRepository<SequenceFile, Long, Integer> {
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

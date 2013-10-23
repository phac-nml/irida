package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;

/**
 * A repository to store information about sequence files. This repository will
 * not directly store the file, just metadata
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */

public interface SequenceFileRepository extends PagingAndSortingRepository<SequenceFile, Long> {
	/**
	 * Get the {@link SequenceFile}s associated with a {@link MiseqRun}
	 * 
	 * @param run
	 *            The {@link MiseqRun} to get the files for
	 * @return a list of {@link MiseqRunSequenceFileJoin} objects
	 */
	public List<MiseqRunSequenceFileJoin> getFilesForMiseqRun(MiseqRun run);

}

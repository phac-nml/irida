package ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;

/**
 * Repository for managing {@link MiseqRunSequenceFileJoin}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface MiseqRunSequenceFileJoinRepository extends CrudRepository<MiseqRunSequenceFileJoin, Long> {
	/**
	 * Get the {@link SequenceFile}s associated with a {@link MiseqRun}
	 * 
	 * @param run
	 *            The {@link MiseqRun} to get the files for
	 * @return a list of {@link MiseqRunSequenceFileJoin} objects
	 */
	@Query("select j from MiseqRunSequenceFileJoin j where j.miseqRun = ?1")
	public List<Join<MiseqRun, SequenceFile>> getFilesForMiseqRun(MiseqRun run);
}

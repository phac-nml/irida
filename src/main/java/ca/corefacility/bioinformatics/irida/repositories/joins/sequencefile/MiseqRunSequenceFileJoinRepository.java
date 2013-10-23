package ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
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

	/**
	 * Get the {@link MiseqRun} associated with a {@link SequenceFile}
	 * 
	 * @param file
	 *            The {@link SequenceFile} to find the run for
	 * @return A {@link MiseqRunSequenceFileJoin} describing the relationship
	 *         between the run and file
	 * @throws EntityNotFoundException
	 *             If the {@link SequenceFile} is not associated with a
	 *             {@link MiseqRun}
	 * @throws StorageException
	 *             If the {@link SequenceFile} is associated with multiple
	 *             {@link MiseqRun}s
	 */
	@Query("select j from MiseqRunSequenceFileJoin j where j.sequenceFile = ?1")
	public Join<MiseqRun, SequenceFile> getMiseqRunForSequenceFile(SequenceFile file);
}

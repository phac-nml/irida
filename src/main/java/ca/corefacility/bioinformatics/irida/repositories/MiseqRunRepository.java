
package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface MiseqRunRepository extends PagingAndSortingRepository<MiseqRun, Long>{
    
    /**
     * Associate a {@link SequenceFile} with a {@link MiseqRun}
     * @param run The {@link MiseqRun} to add to
     * @param file The {@link SequenceFile} to add to the run
     * @return A {@link MiseqRunSequenceFileJoin} describing the relationship between the run and file
     * @throws EntityExistsException If the sequencefile is already associated with a miseq run.
     */
    public MiseqRunSequenceFileJoin addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) throws EntityExistsException;
    
    /**
     * Get the {@link MiseqRun} associated with a {@link SequenceFile}
     * @param file The {@link SequenceFile} to find the run for
     * @return A {@link MiseqRunSequenceFileJoin} describing the relationship between the run and file
     * @throws EntityNotFoundException If the {@link SequenceFile} is not associated with a {@link MiseqRun}
     * @throws StorageException If the {@link SequenceFile} is associated with multiple {@link MiseqRun}s
     */
    public MiseqRunSequenceFileJoin getMiseqRunForSequenceFile(SequenceFile file) throws EntityNotFoundException, StorageException;
}

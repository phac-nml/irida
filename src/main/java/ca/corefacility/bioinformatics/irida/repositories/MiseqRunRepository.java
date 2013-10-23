
package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface MiseqRunRepository extends PagingAndSortingRepository<MiseqRun, Long>{
    
    
    /**
     * Get the {@link MiseqRun} associated with a {@link SequenceFile}
     * @param file The {@link SequenceFile} to find the run for
     * @return A {@link MiseqRunSequenceFileJoin} describing the relationship between the run and file
     * @throws EntityNotFoundException If the {@link SequenceFile} is not associated with a {@link MiseqRun}
     * @throws StorageException If the {@link SequenceFile} is associated with multiple {@link MiseqRun}s
     */
    public MiseqRunSequenceFileJoin getMiseqRunForSequenceFile(SequenceFile file) throws EntityNotFoundException, StorageException;
}

package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.SequencingRun;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface SequencingRunRepository extends PagingAndSortingRepository<SequencingRun, Long> {

}

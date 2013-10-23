package ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile;

import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileOverrepresentedSequenceJoin;

/**
 * Repository for managing {@link SequenceFileOverrepresentedSequenceJoin}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface SequenceFileOverrepresentedSequenceJoinRepository extends
		CrudRepository<SequenceFileOverrepresentedSequenceJoin, Long> {

}

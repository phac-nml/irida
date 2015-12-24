package ca.corefacility.bioinformatics.irida.repositories.joins.sample;

import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;

/**
 * Repository for storing and retrieving {@link SampleSequencingObjectJoin}s
 */
public interface SampleSequencingObjectJoinRepository extends CrudRepository<SampleSequencingObjectJoin, Long> {

}

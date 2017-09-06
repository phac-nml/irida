package ca.corefacility.bioinformatics.irida.repositories.joins.sample;

import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;

/**
 * Repository for storing and retrieving {@link SampleGenomeAssemblyJoin}s.
 */
public interface SampleGenomeAssemblyJoinRepository extends CrudRepository<SampleGenomeAssemblyJoin, Long> {
}

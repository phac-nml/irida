package ca.corefacility.bioinformatics.irida.repositories.joins.sample;

import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;

/**
 * A repository for storing and loading a {@link GenomeAssembly}.
 */
public interface GenomeAssemblyRepository extends CrudRepository<GenomeAssembly, Long> {

}

package ca.corefacility.bioinformatics.irida.repositories.joins.sample;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Repository for storing and retrieving {@link SampleGenomeAssemblyJoin}s.
 */
public interface SampleGenomeAssemblyJoinRepository extends CrudRepository<SampleGenomeAssemblyJoin, Long> {

	/**
	 * Gets a collection of {@link SampleGenomeAssemblyJoin} by the sample.
	 *
	 * @param sample
	 *            The sample.
	 * @return A collection of {@link SampleGenomeAssemblyJoin} by the sample.
	 */
	@Query("FROM SampleGenomeAssemblyJoin j WHERE j.sample = ?1")
	public Collection<SampleGenomeAssemblyJoin> findBySample(Sample sample);

	/**
	 * Gets a {@link GenomeAssembly} from a {@link Sample} with the given id.
	 *
	 * @param sampleId
	 *            The sample ID.
	 * @param assemblyId
	 *            The assembly id.
	 * @return The {@link GenomeAssembly}.
	 */
	@Query("FROM SampleGenomeAssemblyJoin j WHERE j.sample.id = ?1 AND j.genomeAssembly.id = ?2")
	public SampleGenomeAssemblyJoin findBySampleAndAssemblyId(Long sampleId, Long assemblyId);
}

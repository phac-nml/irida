package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

public interface GenomeAssemblyService extends CRUDService<Long, GenomeAssembly> {
	public SampleGenomeAssemblyJoin createAssemblyInSample(Sample sample, GenomeAssembly assembly);

	public Collection<SampleGenomeAssemblyJoin> getAssembliesForSample(Sample sample);

	/**
	 * Gets the genome assembly for a sample.
	 *
	 * @param sample           The sample.
	 * @param genomeAssemblyId The id of the genome assembly.
	 * @return The {@link GenomeAssembly} with the given information.
	 */
	public GenomeAssembly getGenomeAssemblyForSample(Sample sample, Long genomeAssemblyId);

	/**
	 * Deletes the given genome assembly from the given sample.
	 *
	 * @param sample           The sample.
	 * @param genomeAssemblyId The genome assembly.
	 */
	public void removeGenomeAssemblyFromSample(Sample sample, Long genomeAssemblyId);
}

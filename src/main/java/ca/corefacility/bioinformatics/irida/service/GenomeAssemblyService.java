package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

public interface GenomeAssemblyService extends CRUDService<Long, GenomeAssembly> {
	public SampleGenomeAssemblyJoin createAssemblyInSample(Sample sample, GenomeAssembly assembly);

	public Collection<SampleGenomeAssemblyJoin> getAssembliesForSample(Sample sample);
}

package ca.corefacility.bioinformatics.irida.service.remote;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import java.util.List;

public interface GenomeAssemblyRemoteService extends RemoteService<UploadedAssembly> {
	public List<UploadedAssembly> getGenomeAssembliesForSample(Sample sample);

	public UploadedAssembly mirrorAssembly(UploadedAssembly seqObject);
}

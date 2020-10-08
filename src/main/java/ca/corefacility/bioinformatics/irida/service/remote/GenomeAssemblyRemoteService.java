package ca.corefacility.bioinformatics.irida.service.remote;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import java.util.List;

/**
 * A service for reading {@link UploadedAssembly}s from a remote location
 */
public interface GenomeAssemblyRemoteService extends RemoteService<UploadedAssembly> {
	/**
	 * List the {@link GenomeAssembly} for a given {@link Sample}
	 *
	 * @param sample the Sample to get assemblies for
	 * @return a list of {@link UploadedAssembly}
	 */
	public List<UploadedAssembly> getGenomeAssembliesForSample(Sample sample);

	/**
	 * Download the given {@link UploadedAssembly} to the local server
	 *
	 * @param seqObject the {@link UploadedAssembly} to download
	 * @return a local copy of the {@link UploadedAssembly}
	 */
	public UploadedAssembly mirrorAssembly(UploadedAssembly seqObject);
}

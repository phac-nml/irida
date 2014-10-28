package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;

/**
 * Repository for reading {@link RemoteSequenceFile}s from a Remote IRIDA
 * installation
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface SequenceFileRemoteRepository extends RemoteRepository<RemoteSequenceFile> {
	/**
	 * Get a local copy of a {@link RemoteSequenceFile}
	 * 
	 * @param sequenceFile
	 *            The {@link RemoteSequenceFile} to get sequence data for
	 * @param api
	 *            The {@link RemoteAPI} this file resides on
	 * @return A temporary {@link Path} to the sequence file data
	 */
	public Path downloadRemoteSequenceFile(RemoteSequenceFile sequenceFile, RemoteAPI api);
}

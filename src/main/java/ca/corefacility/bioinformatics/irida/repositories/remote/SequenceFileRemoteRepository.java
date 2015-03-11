package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.nio.file.Path;

import org.springframework.http.MediaType;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;

/**
 * Repository for reading {@link RemoteSequenceFile}s from a Remote IRIDA
 * installation
 * 
 *
 */
public interface SequenceFileRemoteRepository extends RemoteRepository<RemoteSequenceFile> {
	/**
	 * Get a local copy of a {@link RemoteSequenceFile} with a default MediaType
	 * of application/fastq
	 * 
	 * @param sequenceFile
	 *            The {@link RemoteSequenceFile} to get sequence data for
	 * @param api
	 *            The {@link RemoteAPI} this file resides on
	 * @return A temporary {@link Path} to the sequence file data
	 */
	public Path downloadRemoteSequenceFile(RemoteSequenceFile sequenceFile, RemoteAPI api);

	/**
	 * Get a local copy of a {@link RemoteSequenceFile}
	 * 
	 * @param sequenceFile
	 *            The {@link RemoteSequenceFile} to get sequence data for
	 * @param api
	 *            The {@link RemoteAPI} this file resides on
	 * @param mediaTypes
	 *            The media types to request from the remote API
	 * @return A temporary {@link Path} to the sequence file data
	 */
	public Path downloadRemoteSequenceFile(RemoteSequenceFile sequenceFile, RemoteAPI api, MediaType... mediaTypes);
}

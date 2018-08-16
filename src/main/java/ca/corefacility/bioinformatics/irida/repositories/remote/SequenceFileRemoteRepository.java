package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.nio.file.Path;

import org.springframework.http.MediaType;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Repository for reading {@link SequenceFile}s from a Remote IRIDA installation
 * 
 *
 */
public interface SequenceFileRemoteRepository extends RemoteRepository<SequenceFile> {
	/**
	 * Get a local copy of a {@link SequenceFile} with a default MediaType of
	 * application/fastq
	 *
	 * @param uri The URI to get sequence data for
	 * @param api The {@link RemoteAPI} this file resides on
	 * @return A temporary {@link Path} to the sequence file data
	 */
	public Path downloadRemoteSequenceFile(String uri, RemoteAPI api);

	/**
	 * Get a local copy of a {@link SequenceFile}
	 *
	 * @param uri        the URI of the file to download
	 * @param api        The {@link RemoteAPI} this file resides on
	 * @param mediaTypes The media types to request from the remote API
	 * @return A temporary {@link Path} to the sequence file data
	 */
	public Path downloadRemoteSequenceFile(String uri, RemoteAPI api, MediaType... mediaTypes);
}

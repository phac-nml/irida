package ca.corefacility.bioinformatics.irida.service.remote;

import java.nio.file.Path;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;

/**
 * Service for reading {@link RemoteSequenceFile}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface SequenceFileRemoteService extends RemoteService<RemoteSequenceFile> {

	/**
	 * Get the list of sequence files in a {@link RemoteSample}
	 * 
	 * @param sample
	 *            The {@link RemoteSample} to read
	 * @param api
	 *            The {@link RemoteAPI} the sample exists on
	 * @return A list of {@link RemoteSequenceFile}s
	 */
	public List<RemoteSequenceFile> getSequenceFilesForSample(RemoteSample sample, RemoteAPI api);

	/**
	 * Download a {@link RemoteSequenceFile} locally
	 * 
	 * @param sequenceFile
	 *            The {@link RemoteSequenceFile} object we want to get the
	 *            sequence data for
	 * @param api
	 *            the {@link RemoteAPI} the sequence file exists on
	 * @return A temporary {@link Path} object for the downloaded file
	 */
	public Path downloadSequenceFile(RemoteSequenceFile sequenceFile, RemoteAPI api);
}

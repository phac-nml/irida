package ca.corefacility.bioinformatics.irida.service.remote;

import java.nio.file.Path;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Service for reading {@link RemoteSequenceFile}s
 * 
 *
 */
public interface SequenceFileRemoteService extends RemoteService<SequenceFile> {

	/**
	 * Get the list of sequence files in a {@link RemoteSample}
	 * 
	 * @param sample
	 *            The {@link RemoteSample} to read
	 * @return A list of {@link RemoteSequenceFile}s
	 */
	public List<SequenceFile> getSequenceFilesForSample(Sample sample);

	/**
	 * Download a {@link RemoteSequenceFile} locally
	 * 
	 * @param sequenceFile
	 *            The {@link RemoteSequenceFile} object we want to get the
	 *            sequence data for
	 * @return A temporary {@link Path} object for the downloaded file
	 */
	public Path downloadSequenceFile(SequenceFile sequenceFile);
}

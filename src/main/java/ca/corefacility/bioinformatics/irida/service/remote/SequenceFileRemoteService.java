package ca.corefacility.bioinformatics.irida.service.remote;

import java.nio.file.Path;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

/**
 * Service for reading {@link SequenceFile}s
 * 
 *
 */
public interface SequenceFileRemoteService extends RemoteService<SequenceFile> {

	/**
	 * Get the list of sequence files in a {@link Sample}
	 * 
	 * @param sample
	 *            The {@link Sample} to read
	 * @return A list of {@link SequenceFile}s
	 */
	public List<SequenceFile> getSequenceFilesForSample(Sample sample);

	/**
	 * Get the {@link SequenceFilePair}s for a given remote {@link Sample}
	 * 
	 * @param sample
	 *            The {@link Sample} to get pairs for
	 * @return List of {@link SequenceFilePair}s
	 */
	List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample);

	/**
	 * Get the {@link SequenceFile}s for a given {@link Sample} that do not have
	 * pairs
	 * 
	 * @param sample
	 *            The {@link Sample} to get files for
	 * @return List of {@link SequenceFile}s
	 */
	List<SequenceFile> getUnpairedSequenceFilesForSample(Sample sample);

	/**
	 * Download a {@link SequenceFile} locally
	 * 
	 * @param sequenceFile
	 *            The {@link SequenceFile} object we want to get the sequence
	 *            data for
	 * @return A temporary {@link Path} object for the downloaded file
	 */
	public Path downloadSequenceFile(SequenceFile sequenceFile);
}

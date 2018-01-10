package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

/**
 * Remote service for reading sequence file pairs from a remote api
 */
public interface SequenceFilePairRemoteService extends SequencingObjectRemoteService<SequenceFilePair> {
	/**
	 * Get the {@link SequenceFilePair}s for a given remote {@link Sample}
	 * 
	 * @param sample
	 *            The {@link Sample} to get pairs for
	 * @return List of {@link SequenceFilePair}s
	 */
	List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample);
}

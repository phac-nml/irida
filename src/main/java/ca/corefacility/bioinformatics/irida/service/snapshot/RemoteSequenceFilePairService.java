package ca.corefacility.bioinformatics.irida.service.snapshot;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for storing and retrieving local snapshots of
 * {@link RemoteSequenceFilePair}s
 * 
 * @see RemoteSequenceFile
 */
public interface RemoteSequenceFilePairService extends CRUDService<Long, RemoteSequenceFilePair> {
	/**
	 * Mirror the metadata for a {@link SequenceFilePair} locally and return a
	 * {@link RemoteSequenceFilePair}. This method will convert the
	 * {@link SequenceFilePair} to a {@link RemoteSequenceFilePair} and the
	 * embedded {@link SequenceFile}s to {@link RemoteSequenceFilePair}s. The
	 * {@link RemoteSequenceFile}s will also be persisted.
	 * 
	 * @param pair
	 *            The {@link SequenceFilePair} to mirror
	 * @return persisted {@link RemoteSequenceFilePair}
	 */
	public RemoteSequenceFilePair mirrorPair(SequenceFilePair pair);
}

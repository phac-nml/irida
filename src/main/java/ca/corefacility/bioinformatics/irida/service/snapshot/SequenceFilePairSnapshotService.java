package ca.corefacility.bioinformatics.irida.service.snapshot;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePairSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for storing and retrieving local snapshots of
 * {@link SequenceFilePairSnapshot}s
 * 
 * @see SequenceFileSnapshot
 */
public interface SequenceFilePairSnapshotService extends CRUDService<Long, SequenceFilePairSnapshot> {
	/**
	 * Mirror the metadata for a {@link SequenceFilePair} locally and return a
	 * {@link SequenceFilePairSnapshot}. This method will convert the
	 * {@link SequenceFilePair} to a {@link SequenceFilePairSnapshot} and the
	 * embedded {@link SequenceFile}s to {@link SequenceFilePairSnapshot}s. The
	 * {@link SequenceFileSnapshot}s will also be persisted.
	 * 
	 * @param pair
	 *            The {@link SequenceFilePair} to mirror
	 * @return persisted {@link SequenceFilePairSnapshot}
	 */
	public SequenceFilePairSnapshot mirrorPair(SequenceFilePair pair);
}

package ca.corefacility.bioinformatics.irida.service.snapshot;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePairSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

public interface SingleEndSequenceFileSnapshotService extends CRUDService<Long, SingleEndSequenceFileSnapshot> {
	/**
	 * Mirror the metadata for a {@link SingleEndSequenceFile} locally and
	 * return a {@link SingleEndSequenceFileSnapshot}. This method will convert
	 * the {@link SingleEndSequenceFile} to a
	 * {@link SingleEndSequenceFileSnapshotService} and the embedded
	 * {@link SequenceFile}s to {@link SequenceFilePairSnapshot}s. The
	 * {@link SequenceFileSnapshot}s will also be persisted.
	 * 
	 * @param file
	 *            The {@link SingleEndSequenceFile} to mirror
	 * @return persisted {@link SingleEndSequenceFileSnapshot}
	 */
	public SingleEndSequenceFileSnapshot mirrorFile(SingleEndSequenceFile file);
}

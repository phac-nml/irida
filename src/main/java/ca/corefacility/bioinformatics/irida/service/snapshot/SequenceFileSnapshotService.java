package ca.corefacility.bioinformatics.irida.service.snapshot;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

public interface SequenceFileSnapshotService extends CRUDService<Long, SequenceFileSnapshot> {

	/**
	 * Mirror the metadata for a {@link SequenceFile} locally and return a
	 * {@link SequenceFileSnapshot}
	 * 
	 * @param file
	 *            The {@link SequenceFile} to mirror
	 * @return persisted {@link SequenceFileSnapshot}
	 */
	public SequenceFileSnapshot mirrorFile(SequenceFile file);

	/**
	 * Download the content of a {@link SequenceFileSnapshot} to the local
	 * filesystem
	 * 
	 * @param snapshot
	 *            the {@link SequenceFileSnapshot} to download remote data for
	 * @return The completed {@link SequenceFileSnapshot}
	 */
	public SequenceFileSnapshot mirrorFileContent(SequenceFileSnapshot snapshot);
}

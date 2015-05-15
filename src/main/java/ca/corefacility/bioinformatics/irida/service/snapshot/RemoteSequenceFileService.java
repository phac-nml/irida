package ca.corefacility.bioinformatics.irida.service.snapshot;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

public interface RemoteSequenceFileService extends CRUDService<Long, RemoteSequenceFile> {

	/**
	 * Mirror the metadata for a {@link SequenceFile} locally and return a
	 * {@link RemoteSequenceFile}
	 * 
	 * @param file
	 *            The {@link SequenceFile} to mirror
	 * @return persisted {@link RemoteSequenceFile}
	 */
	public RemoteSequenceFile mirrorFile(SequenceFile file);
}

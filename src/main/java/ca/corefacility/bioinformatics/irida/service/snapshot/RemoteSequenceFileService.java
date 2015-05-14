package ca.corefacility.bioinformatics.irida.service.snapshot;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

public interface RemoteSequenceFileService extends CRUDService<Long, RemoteSequenceFile> {

	/**
	 * Mirror a {@link RemoteSequenceFile} locally. This may be a long running
	 * method.
	 * 
	 * @param file
	 *            The {@link RemoteSequenceFile} to mirror
	 * @return Updated copy of the file with the local path
	 */
	public RemoteSequenceFile mirrorSequenceFile(RemoteSequenceFile file);
}

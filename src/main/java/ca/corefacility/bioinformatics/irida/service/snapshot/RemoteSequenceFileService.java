package ca.corefacility.bioinformatics.irida.service.snapshot;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

public interface RemoteSequenceFileService extends CRUDService<Long, RemoteSequenceFile> {

	public RemoteSequenceFile mirrorFile(SequenceFile file);
}

package ca.corefacility.bioinformatics.irida.service.snapshot;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFilePair;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for storing and retrieving local snapshots of
 * {@link RemoteSequenceFilePair}s
 */
public interface RemoteSequenceFilePairService extends CRUDService<Long, RemoteSequenceFilePair> {

}

package ca.corefacility.bioinformatics.irida.repositories.remote;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;

/**
 * Repository for synchronizing {@link Fast5Object} from a {@link RemoteAPI}
 */
public interface Fast5ObjectRemoteRepository extends RemoteRepository<Fast5Object> {

}

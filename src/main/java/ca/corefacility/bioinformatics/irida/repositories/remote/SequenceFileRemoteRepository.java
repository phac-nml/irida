package ca.corefacility.bioinformatics.irida.repositories.remote;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;

/**
 * Repository for reading {@link RemoteSequenceFile}s from a Remote IRIDA
 * installation
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface SequenceFileRemoteRepository extends RemoteRepository<RemoteSequenceFile> {

}

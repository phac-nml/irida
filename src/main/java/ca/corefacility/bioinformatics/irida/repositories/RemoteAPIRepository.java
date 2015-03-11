package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * A repository to store RemoteAPI instances that this IRIDA instance can connect to
 *
 */
public interface RemoteAPIRepository extends IridaJpaRepository<RemoteAPI, Long>{

}

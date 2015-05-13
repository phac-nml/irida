package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Service for storing and retrieving {@link RemoteAPI}s that this API can connect to
 *
 */
public interface RemoteAPIService extends CRUDService<Long, RemoteAPI> {
		
}

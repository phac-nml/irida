package ca.corefacility.bioinformatics.irida.service;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Service for storing and retrieving {@link RemoteAPI}s that this API can
 * connect to
 *
 */
public interface RemoteAPIService extends CRUDService<Long, RemoteAPI> {

	@Override
	@PreAuthorize("permitAll")
	public RemoteAPI read(Long id);

	@Override
	@PreAuthorize("permitAll")
	public Iterable<RemoteAPI> findAll();

	@PreAuthorize("permitAll")
	public RemoteAPI getRemoteAPIForUrl(String url);

}

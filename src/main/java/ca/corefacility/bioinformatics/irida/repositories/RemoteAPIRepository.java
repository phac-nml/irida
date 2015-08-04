package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * A repository to store RemoteAPI instances that this IRIDA instance can
 * connect to
 *
 */
public interface RemoteAPIRepository extends IridaJpaRepository<RemoteAPI, Long> {

	/**
	 * Find the {@link RemoteAPI} where the serviceURI is a substring of the
	 * given URL
	 * 
	 * @param url
	 *            The URL to test
	 * @return The {@link RemoteAPI} matching the given URL
	 */
	@Query("FROM RemoteAPI api WHERE locate(api.serviceURI, ?1) = 1")
	public RemoteAPI getRemoteAPIForUrl(String url);
}

package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Repository for storing an reading {@link RemoteAPIToken}s
 */
public interface RemoteApiTokenRepository
		extends CrudRepository<RemoteAPIToken, Long>, RevisionRepository<RemoteAPIToken, Long, Integer> {

	/**
	 * Get an API token from the repository for a given service and user
	 * 
	 * @param remoteApi the {@link RemoteAPI} to read a token for
	 * @param user      The {@link User} to get a token for
	 * @return A current {@link RemoteAPIToken} if one exists
	 */
	@Query("FROM RemoteAPIToken t WHERE t.remoteApi=?1 AND t.user=?2")
	public RemoteAPIToken readTokenForApiAndUser(RemoteAPI remoteApi, User user);

}

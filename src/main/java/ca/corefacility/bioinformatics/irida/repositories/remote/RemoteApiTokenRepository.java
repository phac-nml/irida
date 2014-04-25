package ca.corefacility.bioinformatics.irida.repositories.remote;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.User;

public interface RemoteApiTokenRepository extends CrudRepository<RemoteAPIToken, Long>{
		
	@Query("FROM RemoteAPIToken t WHERE t.remoteApi=?1 AND t.user=?2")
	public RemoteAPIToken readForApiAndUser(RemoteAPI remoteApi,User user);
}

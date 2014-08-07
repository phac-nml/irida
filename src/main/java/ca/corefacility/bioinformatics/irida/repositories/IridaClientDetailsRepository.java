package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;

/**
 * Repository for storing and retriving {@link IridaClientDetails}.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface IridaClientDetailsRepository extends IridaJpaRepository<IridaClientDetails, Long> {

	@Query("from IridaClientDetails d where d.clientId = ?1")
	public IridaClientDetails loadClientDetailsByClientId(String clientId);
}

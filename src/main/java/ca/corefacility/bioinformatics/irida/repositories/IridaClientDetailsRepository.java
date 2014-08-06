package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.repositories.pagingsortingspecification.PagingSortingSpecificationRepository;

/**
 * Repository for storing and retriving {@link IridaClientDetails}.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface IridaClientDetailsRepository extends PagingSortingSpecificationRepository<IridaClientDetails, Long>,
		RevisionRepository<IridaClientDetails, Long, Integer> {

	@Query("from IridaClientDetails d where d.clientId = ?1")
	public IridaClientDetails loadClientDetailsByClientId(String clientId);
}

package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;

public interface ClientDetailsRepository extends PagingAndSortingRepository<IridaClientDetails, Long>,
		RevisionRepository<IridaClientDetails, Long, Integer> {

	@Query("from IridaClientDetails d where d.clientId = ?1")
	public IridaClientDetails loadClientDetailsByClientId(String clientId);
}

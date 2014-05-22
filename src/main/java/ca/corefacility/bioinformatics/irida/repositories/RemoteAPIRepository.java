package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * A repository to store RemoteAPI instances that this IRIDA instance can connect to
 * @author "Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>"
 *
 */
public interface RemoteAPIRepository extends PagingAndSortingRepository<RemoteAPI, Long>{

}

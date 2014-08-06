package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.pagingsortingspecification.PagingSortingSpecificationRepository;

/**
 * A repository to store RemoteAPI instances that this IRIDA instance can connect to
 * @author "Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>"
 *
 */
public interface RemoteAPIRepository extends PagingSortingSpecificationRepository<RemoteAPI, Long>{

}

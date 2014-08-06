package ca.corefacility.bioinformatics.irida.repositories.pagingsortingspecification;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository type that extends PagingAndSortingRepository<Type, Identifier> and
 * JpaSpecificationExecutor<Type>
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 * @param <Type>
 *            Entity type stored by this repository
 * @param <Identifier>
 *            The identifier type for retrieving this entity
 */
@NoRepositoryBean
public interface PagingSortingSpecificationRepository<Type, Identifier extends Serializable> extends
		PagingAndSortingRepository<Type, Identifier>, JpaSpecificationExecutor<Type> {

}

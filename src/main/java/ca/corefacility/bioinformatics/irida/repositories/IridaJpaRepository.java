package ca.corefacility.bioinformatics.irida.repositories;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

/**
 * Repository type that extends PagingAndSortingRepository<Type, Identifier>,
 * JpaSpecificationExecutor<Type>, and RevisionRepository<Type, Identifier,
 * Integer>
 * 
 *
 * @param <Type>
 *            Entity type stored by this repository
 * @param <Identifier>
 *            The identifier type for retrieving this entity
 */
@NoRepositoryBean
public interface IridaJpaRepository<Type, Identifier extends Serializable> extends
		PagingAndSortingRepository<Type, Identifier>, RevisionRepository<Type, Identifier, Integer>,
		JpaSpecificationExecutor<Type> {

}

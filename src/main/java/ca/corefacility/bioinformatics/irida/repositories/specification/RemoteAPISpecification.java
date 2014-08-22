package ca.corefacility.bioinformatics.irida.repositories.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Specification class for {@link RemoteAPI}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class RemoteAPISpecification {

	/**
	 * Search for a RemoteAPI object with a given search term
	 * 
	 * @param searchString
	 *            The string to search
	 * @return a specification for this search
	 */
	public static Specification<RemoteAPI> searchRemoteAPI(String searchString) {
		return new Specification<RemoteAPI>() {
			@Override
			public Predicate toPredicate(Root<RemoteAPI> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.like(root.get("name"), "%" + searchString + "%");
			}

		};
	}
}

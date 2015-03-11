package ca.corefacility.bioinformatics.irida.repositories.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;

/**
 * Specification class for {@link IridaClientDetails}
 * 
 *
 */
public class IridaClientDetailsSpecification {

	/**
	 * Search for a IridaClientDetails object with a given search term
	 * 
	 * @param searchString
	 *            The string to search
	 * @return a specification for this search
	 */
	public static Specification<IridaClientDetails> searchClient(String searchString) {
		return new Specification<IridaClientDetails>() {
			@Override
			public Predicate toPredicate(Root<IridaClientDetails> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.like(root.get("clientId"), "%" + searchString + "%");
			}

		};
	}
}

package ca.corefacility.bioinformatics.irida.repositories.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Specification for searching a {@link User}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class UserSpecification {
	/**
	 * Search for a {@link User} by firstname, lastname, email, and username
	 * 
	 * @param searchString
	 *            The name to search for
	 * @return
	 */
	public static Specification<User> searchUser(String searchString) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(cb.like(root.get("firstName"), "%" + searchString + "%"),
						cb.like(root.get("lastName"), "%" + searchString + "%"),
						cb.like(root.get("email"), "%" + searchString + "%"),
						cb.like(root.get("username"), "%" + searchString + "%"));
			}

		};
	}
}

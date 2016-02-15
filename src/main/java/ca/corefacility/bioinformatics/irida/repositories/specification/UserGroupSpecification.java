package ca.corefacility.bioinformatics.irida.repositories.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;

/**
 * Search specification for {@link UserGroup}.
 *
 */
public class UserGroupSpecification {
	/**
	 * Search for {@link UserGroup} by name.
	 * 
	 * @param searchString
	 *            the name to search for.
	 * @return a specification that can be used for the search.
	 */
	public static final Specification<UserGroup> searchUserGroup(final String searchString) {
		return new Specification<UserGroup>() {
			@Override
			public Predicate toPredicate(Root<UserGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.like(root.get("name"), "%" + searchString + "%");
			}
		};
	}
}

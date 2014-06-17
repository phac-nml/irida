package ca.corefacility.bioinformatics.irida.repositories.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Specification for searching {@link ProjectUserJoin}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class ProjectUserJoinSpecification {

	/**
	 * Get a {@link ProjectUserJoin} where the project name contains the given
	 * search string and have a given {@link User}
	 * 
	 * @param name
	 *            The name to search
	 * @param user
	 *            The user to search
	 * @return
	 */
	public static Specification<ProjectUserJoin> searchProjectNameWithUser(String name, User user) {
		return new Specification<ProjectUserJoin>() {
			@Override
			public Predicate toPredicate(Root<ProjectUserJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.like(root.get("project").get("name"), "%" + name + "%"),
						cb.equal(root.get("user"), user));
			}
		};
	}
}

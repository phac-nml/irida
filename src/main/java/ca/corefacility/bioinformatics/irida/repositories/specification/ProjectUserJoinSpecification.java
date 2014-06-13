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
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class ProjectUserJoinSpecification {

	/**
	 * Get the projects that have a given user
	 * @param user The user to search
	 * @return
	 */
	public static Specification<ProjectUserJoin> projectHasUser(User user) {
		return new Specification<ProjectUserJoin>() {
			@Override
			public Predicate toPredicate(Root<ProjectUserJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("user"), user);
			}
		};
	}
	
	/**
	 * Get a {@link ProjectUserJoin} where the project name contains the given search string
	 * @param name The name to search
	 * @return
	 */
	public static Specification<ProjectUserJoin> projectSearchName(String name) {
		return new Specification<ProjectUserJoin>() {
			@Override
			public Predicate toPredicate(Root<ProjectUserJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.like(root.get("project").get("name"), "%"+name+"%");
			}
		};
	}
}

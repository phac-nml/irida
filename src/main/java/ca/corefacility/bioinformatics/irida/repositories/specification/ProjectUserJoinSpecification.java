package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Specification for searching {@link ProjectUserJoin}s
 * 
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
	 * @return a {@link Specification} to search for a {@link Project} with the
	 *         specified {@link User}.
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

	/**
	 * Get a {@link ProjectUserJoin} where the user has a given role
	 * 
	 * @param projectRole
	 *            The {@link ProjectRole} to search for.
	 * @param user
	 *            The user to search
	 * @return a {@link Specification} to search for {@link Project} where the
	 *         specified {@link User} has a certain {@link ProjectRole}.
	 */
	public static Specification<ProjectUserJoin> getProjectJoinsWithRole(User user, ProjectRole projectRole) {
		return new Specification<ProjectUserJoin>() {
			@Override
			public Predicate toPredicate(Root<ProjectUserJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("projectRole"), projectRole), cb.equal(root.get("user"), user));
			}
		};
	}

	/**
	 * Exclude the given projects from the results
	 * 
	 * @param projects
	 *            The projects to exclude
	 * @return A specification instructing to exclude the given projects
	 */
	public static Specification<ProjectUserJoin> excludeProject(Project... projects) {
		return new Specification<ProjectUserJoin>() {
			@Override
			public Predicate toPredicate(Root<ProjectUserJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				ArrayList<Predicate> predicates = new ArrayList<>();
				for (Project p : projects) {
					predicates.add(cb.notEqual(root.get("project"), p));
				}

				return cb.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		};
	}
}

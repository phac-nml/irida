package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

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

	/**
	 * Search for projects belonging to a specific user based on search criteria.
	 *
	 * @param user
	 * 		{@link User} User to get the projects for.
	 * @param searchMap
	 * 		{@link Map} where key corresponds to {@link Project} attributes to filter by.
	 *
	 * @return {@link Specification}
	 */
	public static Specification<ProjectUserJoin> filterProjectsForUserByProjectAttributes(User user, Map<String, String> searchMap) {
		return (root, query, cb) -> {
			ArrayList<Predicate> predicates = new ArrayList<>();

			if (searchMap.containsKey("name")) {
				predicates.add(cb.like(root.get("project").get("name"), "%" + searchMap.get("name") + "%"));
			}
			if (searchMap.containsKey("organism")) {
				predicates.add(cb.like(root.get("project").get("organism"), "%" + searchMap.get("organism") + "%"));
			}

			predicates.add(cb.equal(root.get("user"), user));

			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	/**
	 * Filter {@link Project}s for a specific {@link User} based on all project attributes.
	 *  (currently only id, name, and organism).
	 *
	 * @param user
	 * 		{@link User} currently logged in {@link User}
	 * @param term
	 * 		{@link String} Search query
	 *
	 * @return {@link Specification}
	 */
	public static Specification<ProjectUserJoin> filterProjectsForUserAllFields(User user, String term) {
		return (root, query, cb) -> {
			ArrayList<Predicate> predicates = new ArrayList<>();
			// Since the project id is a long, we first check to ensure that it is a number being searched
			// If it is, then to get the search to work within a long, we need to cast that id as a string
			// and then proceed with the search.
			if (term.matches("\\d*")) {
				predicates.add(cb.like(root.get("project").get("id").as(String.class), "%" + term + "%"));
			}
			predicates.add(cb.like(root.get("project").get("name"), "%" + term + "%"));
			predicates.add(cb.like(root.get("project").get("organism"), "%" + term + "%"));

			Predicate ors = cb.or(predicates.toArray(new Predicate[predicates.size()]));
			return cb.and(cb.equal(root.get("user"), user), ors);

		};
	}
}

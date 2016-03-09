package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Specification for searching project properties
 * 
 *
 */
public class ProjectSpecification {


	/**
	 * Search through {@link Project}s for a {@link User} based on specific attributes or through a generalized search
	 * string.
	 *
	 * @param filterMap
	 * 		{@link Map} of {@link Project} attributes to search with the searc term.
	 * @param term
	 * 		{@link String} A general search term to search the {@link Project} attributes.
	 *
	 * @return {@link Specification} for the search.
	 */
	public static Specification<Project> filterProjectsByAdvancedFiltersAndSearch(Map<String, String> filterMap, String term) {
		return (root, query, cb) -> {
			Predicate filteredPredicatesList = getFilteredPredicates(cb, root, filterMap);
			Predicate searchPredicatesList = getSearchPredicates(cb, root, term);

			// Filter gets first priority
			
			return cb.and(new Predicate[] {filteredPredicatesList,searchPredicatesList});
		};
	}

	/**
	 * Create a {@link Predicate} based on a {@link Map} of {@link Project} attributes with their corresponding search
	 * string..
	 *
	 * @param cb
	 * 		{@link CriteriaBuilder} for a {@link Specification}
	 * @param root
	 * 		{@link Root}
	 * @param filterMap
	 * 		{@link Map} of {@link Project} attributes to search with their corresponding search term.
	 *
	 * @return {@link Predicate}
	 */
	private static Predicate getFilteredPredicates(CriteriaBuilder cb, Root<Project> root, Map<String, String> filterMap) {
		ArrayList<Predicate> predicates = new ArrayList<>();

		if (filterMap.containsKey("name")) {
			predicates.add(cb.like(root.get("name"), "%" + filterMap.get("name") + "%"));
		}
		if (filterMap.containsKey("organism")) {
			predicates.add(cb.like(root.get("organism"), "%" + filterMap.get("organism") + "%"));
		}
		return cb.and(predicates.toArray(new Predicate[predicates.size()]));
	}

	/**
	 * Create a {@link Predicate} to search 'all' {@link Project} attribute for one general search {@link String}
	 *
	 * @param cb
	 * 		{@link CriteriaBuilder} for a {@link Specification}
	 * @param root
	 * 		{@link Root}
	 * @param term
	 * 		{@link String} term to search the {@link Project} attributes for.
	 *
	 * @return {@link Predicate}
	 */
	private static Predicate getSearchPredicates(CriteriaBuilder cb, Root<Project> root, String term) {
		ArrayList<Predicate> predicates = new ArrayList<>();

		// Since the project id is a long, we first check to ensure that it is a number being searched
		// If it is, then to get the search to work within a long, we need to cast that id as a string
		// and then proceed with the search.
		if (term.matches("\\d*")) {
			predicates.add(cb.like(root.get("id").as(String.class), "%" + term + "%"));
		}
		predicates.add(cb.like(root.get("name"), "%" + term + "%"));
		predicates.add(cb.like(root.get("organism"), "%" + term + "%"));
		return cb.or(predicates.toArray(new Predicate[predicates.size()]));
	}
}

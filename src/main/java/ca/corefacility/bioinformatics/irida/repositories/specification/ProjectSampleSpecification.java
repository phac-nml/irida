package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.google.common.base.Strings;

/**
 * Specification for searching and filtering {@link ProjectSampleJoin} properties
 */
public class ProjectSampleSpecification {
	/**
	 * Search a {@link List} of {@link Project}s for {@link Sample}s that attributes contain the search term.
	 *
	 * @param projects
	 * 		a {@link List} of {@link Project}s
	 * @param searchTerm
	 * 		{@link String} term to search the {@link Sample} attributes for
	 *
	 * @return
	 */
	public static Specification<ProjectSampleJoin> searchProjectSamples(List<Project> projects, String searchTerm) {
		return (root, criteriaQuery, criteriaBuilder) -> {
			List<Predicate> predicateList = new ArrayList<>();
			Expression<Project> projectExpression = root.get("project");

			// Check to see if the search term matches any of the appropriate attributes.
			predicateList.add(criteriaBuilder.like(root.get("sample").get("sampleName"), "%" + searchTerm + "%"));

			return criteriaBuilder.and(
					// This is a check to see if the project is in the list of projects requested.
					projectExpression.in(projects),
					// Search the necessary fields.
					criteriaBuilder.or(predicateList.toArray(new Predicate[predicateList.size()])
					));
		};
	}

	/**
	 * Filter a {@link List} of {@link Project}s for {@link Sample}s whose attributes contain the filtered terms.
	 * @param projects
	 * 		{@link List} of {@link Project}s
	 * @param name
	 * 		{@link String} to search for {@link Sample} that have the term in their name.
	 * @param minDate
	 * 		Minimum {@link Date} the project was modified.
	 * @param maxDate
	 * 		Maximum {@link Date} the project was modified.
	 * @return
	 */
	public static Specification<ProjectSampleJoin> filterProjectSamples(List<Project> projects, String name,
			Date minDate, Date maxDate) {
		return (root, criteriaQuery, criteriaBuilder) -> {
			List<Predicate> predicateList = new ArrayList<>();
			Expression<Project> exp = root.get("project");

			// This is a check to see if the project is in the list of projects requested.
			predicateList.add(exp.in(projects));

			if (!Strings.isNullOrEmpty(name)) {
				predicateList
						.add(criteriaBuilder.like(root.get("sample").get("sampleName"), "%" + name + "%"));
			}
			//			if (!Strings.isNullOrEmpty((String)filter.get("organism"))) {
			//				predicateList.add(criteriaBuilder.like(root.get("sample").get("organism"), "%" + filter.get("organism") + "%"));
			//			}
			//			if (filter.get("minDate") != null) {
			//				predicateList.add(criteriaBuilder.greaterThanOrEqualTo(
			//						root.get("createdDate"), (Date)filter.get("minDate")));
			//			}
			//			if (filter.get("maxDate") != null) {
			//				predicateList.add(criteriaBuilder
			//						.lessThanOrEqualTo(root.get("createdDate"), (Date)filter.get("maxDate")));
			//			}
			if (predicateList.size() > 0) {
				return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
			} else {
				return null;
			}
		};
	}
}

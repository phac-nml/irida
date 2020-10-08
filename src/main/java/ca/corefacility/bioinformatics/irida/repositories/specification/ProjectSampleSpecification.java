package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.google.common.base.Strings;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Specification for searching and filtering {@link ProjectSampleJoin} properties
 */
public class ProjectSampleSpecification {

	/**
	 * Search a {@link Project} and it's associated {@link Project}s for {@link Sample}s based on filtering criteria.
	 *
	 * @param projects    {@link List} of {@link Project} the {@link Sample}s must be found within.
	 * @param sampleNames {@link List} of {@link String} of Sample names to search
	 * @param sampleName  A single {@link Sample} name to loosely search
	 * @param searchTerm  {@link String} search term to search for.
	 * @param organism    {@link String} organism to search for.
	 * @param minDate     {@link Date} minimum date the sample was modified.
	 * @param maxDate     {@link Date} maximum date the sample was modified.
	 * @return {@link Specification} of {@link ProjectSampleJoin} for criteria to search based on the filtered criteria.
	 */
	public static Specification<ProjectSampleJoin> getSamples(List<Project> projects, List<String> sampleNames,
			String sampleName, String searchTerm, String organism, Date minDate, Date maxDate) {
		return (root, criteriaQuery, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// Make sure the project is in the list of project requested;
			Expression<Project> projectExpression = root.get("project");
			predicates.add(projectExpression.in(projects));

			// Check to see if the sampleNames are in the samples
			if (sampleNames.size() > 0) {
				Expression<String> sampleNameExpression = root.get("sample").get("sampleName");
				predicates.add(sampleNameExpression.in(sampleNames));
			}

			// Check to see if there is a specific sample name
			if (!Strings.isNullOrEmpty(sampleName)) {
				predicates.add(criteriaBuilder.like(root.get("sample").get("sampleName"), "%" + sampleName + "%"));
			}
			// Check for the table search.
			// This can be expanded in future to search any attribute on the sample (e.g. description)
			// Underscores within the search term are escaped as the underscores were being treated the same 
			// as hyphens.
			if (!Strings.isNullOrEmpty(searchTerm)) {
				predicates.add(criteriaBuilder.like(root.get("sample").get("sampleName"), "%" + searchTerm.replace("_", "\\_") + "%"));
			}
			// Check for organism
			if (!Strings.isNullOrEmpty(organism)) {
				predicates.add(criteriaBuilder.like(root.get("sample").get("organism"), "%" + organism + "%"));
			}
			// Check if there is a minimum search date
			if (minDate != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(
						root.get("sample").get("modifiedDate"), minDate));
			}
			if (maxDate != null) {
				predicates.add(criteriaBuilder
						.lessThanOrEqualTo(root.get("sample").get("modifiedDate"), maxDate));
			}
			if (predicates.size() > 0) {
				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			} else {
				return null;
			}
		};
	}
}

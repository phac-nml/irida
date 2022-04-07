package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.*;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.google.common.base.Strings;

/**
 * Specification for searching and filtering {@link ProjectSampleJoin} properties
 */
public class ProjectSampleSpecification implements Specification<ProjectSampleJoin> {

	private List<SearchCriteria> list;

	public ProjectSampleSpecification() {
		this.list = new ArrayList<>();
	}

	public void add(SearchCriteria criteria) {
		list.add(criteria);
	}

	@Override
	public Predicate toPredicate(Root<ProjectSampleJoin> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		//create a new predicate list
		List<Predicate> predicates = new ArrayList<>();

		//add criteria to predicates
		for (SearchCriteria criteria : list) {
			Path<?> path = getPath(root, criteria.getKey());

			if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
				if (path.getJavaType() == Date.class) {
					predicates.add(builder.greaterThan(path.as(Date.class), (Date) criteria.getValue()));
				} else {
					predicates.add(builder.greaterThan(path.as(String.class), criteria.getValue().toString()));
				}
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
				if (path.getJavaType() == Date.class) {
					predicates.add(builder.lessThan(path.as(Date.class), (Date) criteria.getValue()));
				} else {
					predicates.add(builder.lessThan(path.as(String.class), criteria.getValue().toString()));
				}
			} else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
				if (path.getJavaType() == Date.class) {
					predicates.add(builder.greaterThanOrEqualTo(path.as(Date.class), (Date) criteria.getValue()));
				} else {
					predicates.add(builder.greaterThanOrEqualTo(path.as(String.class), criteria.getValue().toString()));
				}
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
				if (path.getJavaType() == Date.class) {
					predicates.add(builder.lessThanOrEqualTo(path.as(Date.class), (Date) criteria.getValue()));
				} else {
					predicates.add(builder.lessThanOrEqualTo(path.as(String.class), criteria.getValue().toString()));
				}
			} else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
				predicates.add(builder.notEqual(path, criteria.getValue()));
			} else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
				predicates.add(builder.equal(path, criteria.getValue()));
			} else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
				predicates.add(builder.like(builder.lower(path.as(String.class)),
						"%" + criteria.getValue().toString().toLowerCase() + "%"));
			} else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
				predicates.add(builder.like(builder.lower(path.as(String.class)),
						criteria.getValue().toString().toLowerCase() + "%"));
			} else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
				predicates.add(builder.like(builder.lower(path.as(String.class)),
						"%" + criteria.getValue().toString().toLowerCase()));
			} else if (criteria.getOperation().equals(SearchOperation.IN)) {
				predicates.add(builder.in((Path) path).value(criteria.getValue()));
			} else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
				predicates.add(builder.not((Path) path).in(criteria.getValue()));
			}
		}

		return builder.and(predicates.toArray(new Predicate[0]));
	}

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
	public static ProjectSampleSpecification getSamples(List<Project> projects, List<String> sampleNames,
			String sampleName, String searchTerm, String organism, Date minDate, Date maxDate) {

		ProjectSampleSpecification psFilterSpec = new ProjectSampleSpecification();

		// Make sure the project is in the list of projects requested
		psFilterSpec.add(new SearchCriteria("project", projects, SearchOperation.IN));

		// Check to see if the sampleNames are in the samples
		if (sampleNames.size() > 0) {
			psFilterSpec.add(new SearchCriteria("sample.sampleName", sampleNames, SearchOperation.IN));
		}

		// Check to see if there is a specific sample name
		if (!Strings.isNullOrEmpty(sampleName)) {
			psFilterSpec.add(new SearchCriteria("sample.sampleName", sampleName, SearchOperation.MATCH));
		}

		// Check for the table search.
		// This can be expanded in future to search any attribute on the sample (e.g. description)
		// Underscores within the search term are escaped as the underscores were being treated the same
		// as hyphens.
		if (!Strings.isNullOrEmpty(searchTerm)) {
			psFilterSpec.add(
					new SearchCriteria("sample.sampleName", searchTerm.replace("_", "\\_"), SearchOperation.MATCH));
		}

		// Check for organism
		if (!Strings.isNullOrEmpty(organism)) {
			psFilterSpec.add(new SearchCriteria("sample.organism", organism, SearchOperation.MATCH));
		}

		// Check if there is a minimum search date
		if (minDate != null) {
			psFilterSpec.add(new SearchCriteria("sample.modifiedDate", minDate, SearchOperation.GREATER_THAN_EQUAL));
		}

		// Check if there is a maximum search date
		if (maxDate != null) {
			psFilterSpec.add(new SearchCriteria("sample.modifiedDate", maxDate, SearchOperation.LESS_THAN_EQUAL));
		}

		return psFilterSpec;
	}

	private Path<?> getPath(Root<ProjectSampleJoin> root, String attributeName) {
		Path<?> path;
		if (attributeName.contains(".")) {
			String[] split = attributeName.split("\\.");
			path = root.get(split[0]);
			for (int i = 1; i < split.length; i++) {
				path = path.get(split[i]);
			}
		} else {
			path = root.get(attributeName);
		}
		return path;
	}

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
	public static Specification<ProjectSampleJoin> getSamplesOld(List<Project> projects, List<String> sampleNames,
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
				predicates.add(criteriaBuilder.like(root.get("sample").get("sampleName"),
						"%" + searchTerm.replace("_", "\\_") + "%"));
			}
			// Check for organism
			if (!Strings.isNullOrEmpty(organism)) {
				predicates.add(criteriaBuilder.like(root.get("sample").get("organism"), "%" + organism + "%"));
			}
			// Check if there is a minimum search date
			if (minDate != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sample").get("modifiedDate"), minDate));
			}
			if (maxDate != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("sample").get("modifiedDate"), maxDate));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}

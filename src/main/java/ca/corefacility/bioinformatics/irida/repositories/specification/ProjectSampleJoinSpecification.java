package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.*;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;

/**
 * Specification for searching {@link ProjectSampleJoin}s
 */
public class ProjectSampleJoinSpecification implements Specification<ProjectSampleJoin> {

	private List<SearchCriteria> list;

	public ProjectSampleJoinSpecification() {
		this.list = new ArrayList<>();
	}

	/**
	 * Add a {@link SearchCriteria}
	 *
	 * @param criteria the {@link SearchCriteria} to add to the Specification.
	 */
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
					predicates.add(builder.greaterThan(path.as(Date.class), valueToDate(criteria.getValue())));
				} else {
					predicates.add(builder.greaterThan(path.as(String.class), criteria.getValue().toString()));
				}
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
				if (path.getJavaType() == Date.class) {
					predicates.add(builder.lessThan(path.as(Date.class), valueToDate(criteria.getValue())));
				} else {
					predicates.add(builder.lessThan(path.as(String.class), criteria.getValue().toString()));
				}
			} else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
				if (path.getJavaType() == Date.class) {
					predicates.add(builder.greaterThanOrEqualTo(path.as(Date.class), valueToDate(criteria.getValue())));
				} else {
					predicates.add(builder.greaterThanOrEqualTo(path.as(String.class), criteria.getValue().toString()));
				}
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
				if (path.getJavaType() == Date.class) {
					predicates.add(builder.lessThanOrEqualTo(path.as(Date.class), valueToDate(criteria.getValue())));
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
			} else if (criteria.getOperation().equals(SearchOperation.MATCH_IN)) {
				List<Predicate> matchInPredicates = new ArrayList<>();
				for (Object value : (List<Object>) criteria.getValue()) {
					matchInPredicates.add(builder.like(builder.lower(path.as(String.class)),
							"%" + value.toString().toLowerCase() + "%"));
				}
				predicates.add(builder.or(matchInPredicates.toArray(new Predicate[0])));
			} else if (criteria.getOperation().equals(SearchOperation.IN)) {
				predicates.add(builder.in((Path) path).value(criteria.getValue()));
			} else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
				predicates.add(builder.not((Path) path).in(criteria.getValue()));
			}
		}

		return builder.and(predicates.toArray(new Predicate[0]));
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

	private Date valueToDate(Object value) {
		if (value instanceof Date) {
			return (Date) value;
		} else if (value instanceof Integer) {
			return new Date((Integer) value * 1000L);
		} else {
			return new Date(Long.parseLong(value.toString()) * 1000L);
		}
	}
}

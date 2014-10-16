package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;

import com.google.common.base.Strings;

/**
 * Specification for searching {@link ProjectSampleJoin} properties
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectSampleFilterSpecification {
	public static Specification<ProjectSampleJoin> searchProjectSamples(Project project, String name, String organism, Date minDate, Date maxDate) {
		return new Specification<ProjectSampleJoin>() {
			@Override public Predicate toPredicate(Root<ProjectSampleJoin> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				predicateList.add(criteriaBuilder.equal(root.get("project"), project));
				if (!Strings.isNullOrEmpty(name)) {
					predicateList.add(criteriaBuilder.like(root.get("sample").get("sampleName"), "%" + name + "%"));
				}
				if (!Strings.isNullOrEmpty(organism)) {
					predicateList.add(criteriaBuilder.like(root.get("sample").get("organism"), "%" + organism + "%"));
				}
				if (minDate != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(
							root.get("createdDate"), minDate));
				}
				if (maxDate != null) {
					predicateList.add(criteriaBuilder
							.lessThanOrEqualTo(root.get("createdDate"), maxDate));
				}
				if (predicateList.size() > 0) {
					return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
				} else {
					return null;
				}
			}
		};
	}
}

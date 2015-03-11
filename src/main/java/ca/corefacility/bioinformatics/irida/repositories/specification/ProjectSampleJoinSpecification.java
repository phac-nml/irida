package ca.corefacility.bioinformatics.irida.repositories.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Specification for searching {@link ProjectSampleJoin}s
 * 
 *
 */
public class ProjectSampleJoinSpecification {

	/**
	 * Get a {@link ProjectSampleJoin} where the sample name contains the given
	 * search string and exists in a given Project
	 * 
	 * @param name
	 *            The name to search
	 * @param project
	 *            The project to search
	 * @return
	 */
	public static Specification<ProjectSampleJoin> searchSampleWithNameInProject(String name, Project project) {
		return new Specification<ProjectSampleJoin>() {
			@Override
			public Predicate toPredicate(Root<ProjectSampleJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.like(root.get("sample").get("sampleName"), "%" + name + "%"),
						cb.equal(root.get("project"), project));
			}
		};
	}
}

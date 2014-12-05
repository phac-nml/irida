package ca.corefacility.bioinformatics.irida.repositories.specification;

import java.util.ArrayList;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Specification for searching project properties
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class ProjectSpecification {
	/**
	 * Search for a project by name
	 * 
	 * @param name
	 * @return
	 */
	public static Specification<Project> searchProjectName(String name) {
		return new Specification<Project>() {
			@Override
			public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.like(root.get("name"), "%" + name + "%");
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
	public static Specification<Project> excludeProject(Project... projects) {
		return new Specification<Project>() {
			@Override
			public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				ArrayList<Predicate> predicates = new ArrayList<>();
				for (Project p : projects) {
					predicates.add(cb.notEqual(root, p));
				}

				return cb.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		};
	}
}

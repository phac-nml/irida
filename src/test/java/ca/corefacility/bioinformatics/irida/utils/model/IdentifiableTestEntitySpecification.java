package ca.corefacility.bioinformatics.irida.utils.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * Test specification
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class IdentifiableTestEntitySpecification {
	public static Specification<IdentifiableTestEntity> search() {
		return new Specification<IdentifiableTestEntity>() {
			@Override
			public Predicate toPredicate(Root<IdentifiableTestEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.like(root.get("nonNull"), "not null");
			}

		};
	}
}

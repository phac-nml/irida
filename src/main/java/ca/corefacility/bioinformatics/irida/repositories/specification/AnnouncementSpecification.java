package ca.corefacility.bioinformatics.irida.repositories.specification;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * A specification for searching for {@link Announcement}s
 */
public class AnnouncementSpecification {
	/**
	 * Search for {@link Announcement} by name.
	 *
	 * @param searchString the name to search for.
	 * @return a specification that can be used for the search.
	 */
	public static final Specification<Announcement> searchAnnouncement(final String searchString) {
		return new Specification<Announcement>() {
			@Override
			public Predicate toPredicate(Root<Announcement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.or(cb.like(root.get("message"), "%" + searchString + "%"),
						cb.like(root.get("user").get("username"), "%" + searchString + "%"));
			}
		};
	}
}

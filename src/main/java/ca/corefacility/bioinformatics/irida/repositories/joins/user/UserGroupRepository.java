package ca.corefacility.bioinformatics.irida.repositories.joins.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.UserGroup;

/**
 * Repository for managing {@link UserGroup} -- the relationship between
 * {@link User} and {@link Group}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface UserGroupRepository extends CrudRepository<UserGroup, Long> {

	/**
	 * Get all {@link User} references in a {@link Group}.
	 * 
	 * @param g
	 *            the {@link Group} whose members we want to load.
	 * @return the collection of {@link User} associated with the {@link Group}.
	 */
	@Query("select j from UserGroup j where j.logicalGroup = ?1")
	public List<Join<User, Group>> getUsersForGroup(Group g);
}

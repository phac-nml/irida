package ca.corefacility.bioinformatics.irida.repositories.joins.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.UserGroupJoin;

/**
 * Repository for managing {@link UserGroupJoin} -- the relationship between
 * {@link User} and {@link Group}.
 * 
 *
 */
public interface UserGroupJoinRepository extends CrudRepository<UserGroupJoin, Long> {

	/**
	 * Get all {@link User} references in a {@link Group}.
	 * 
	 * @param g
	 *            the {@link Group} whose members we want to load.
	 * @return the collection of {@link User} associated with the {@link Group}.
	 */
	@Query("select j from UserGroupJoin j where j.logicalGroup = ?1")
	public List<Join<User, Group>> getUsersForGroup(Group g);
}

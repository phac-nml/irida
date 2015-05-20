package ca.corefacility.bioinformatics.irida.service.user;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Specialized service for managing {@link Group}.
 * 
 *
 */
public interface GroupService extends CRUDService<Long, Group> {

	/**
	 * Add a {@link User} to a specific {@link Group}. Both the {@link User} and
	 * the {@link Group} *must* have been previously persisted.
	 * 
	 * @param g
	 *            the {@link Group} to add the {@link User}.
	 * @param u
	 *            the {@link User} to add to the {@link Group}
	 * @return a {@link Join} showing the relationship between {@link User} and
	 *         {@link Group}.
	 * @throws EntityNotFoundException
	 *             if either the {@link User} or {@link Group} cannot be found.
	 * @throws EntityExistsException
	 *             if the {@link User} already belongs to the {@link Group}.
	 */
	public Join<User, Group> addUserToGroup(Group g, User u) throws EntityNotFoundException, EntityExistsException;
}

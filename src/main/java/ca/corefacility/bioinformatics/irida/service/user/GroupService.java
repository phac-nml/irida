package ca.corefacility.bioinformatics.irida.service.user;

import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
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
	 * {@inheritDoc}
	 * 
	 * Must have ROLE_ADMIN to create a new {@link Group}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Group create(@Valid Group object) throws EntityExistsException, ConstraintViolationException;

	/**
	 * {@inheritDoc}
	 * 
	 * Must have ROLE_ADMIN to modify an existing {@link Group}.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Group update(Long id, Map<String, Object> updatedProperties) throws EntityExistsException,
			EntityNotFoundException, ConstraintViolationException, InvalidPropertyException;

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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Join<User, Group> addUserToGroup(Group g, User u) throws EntityNotFoundException, EntityExistsException;
}

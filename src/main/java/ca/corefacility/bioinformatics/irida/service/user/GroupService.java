package ca.corefacility.bioinformatics.irida.service.user;

import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Specialized service for managing {@link Group}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
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
}

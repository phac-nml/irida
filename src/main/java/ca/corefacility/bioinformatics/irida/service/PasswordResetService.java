package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.PasswordReset;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.ConstraintViolationException;

/**
 * Service for managing {@link PasswordReset} entities.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@PreAuthorize("permitAll")
public interface PasswordResetService extends CRUDService<Long, PasswordReset> {

	public PasswordReset createForUser(String email) throws EntityExistsException, ConstraintViolationException;
}

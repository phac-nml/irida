package ca.corefacility.bioinformatics.irida.service.user;

import javax.validation.ConstraintViolationException;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for managing {@link PasswordReset} entities.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@PreAuthorize("permitAll")
public interface PasswordResetService extends CRUDService<String, PasswordReset> {

	@Override
	@PreAuthorize("hasPermission(#passwordReset, 'canCreatePasswordReset')")
	public PasswordReset create(PasswordReset passwordReset) throws EntityExistsException, ConstraintViolationException;
}

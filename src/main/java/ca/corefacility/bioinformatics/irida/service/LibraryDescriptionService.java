package ca.corefacility.bioinformatics.irida.service;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.project.library.LibraryDescription;

/**
 * Service for managing {@link LibraryDescription}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface LibraryDescriptionService extends CRUDService<Long, LibraryDescription> {

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#libraryDescription.project, 'isProjectOwner')")
	public LibraryDescription create(@Valid LibraryDescription libraryDescription) throws EntityExistsException,
			ConstraintViolationException;
}

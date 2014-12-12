package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.project.library.LibraryDescription;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import ca.corefacility.bioinformatics.irida.service.LibraryDescriptionService;

/**
 * Service implementation for managing {@link LibraryDescription}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Service
public class LibraryDescriptionServiceImpl extends CRUDServiceImpl<Long, LibraryDescription> implements
		LibraryDescriptionService {

	@Autowired
	public LibraryDescriptionServiceImpl(final IridaJpaRepository<LibraryDescription, Long> repository,
			final Validator validator) {
		super(repository, validator, LibraryDescription.class);
	}
	
	@Override
	public LibraryDescription create(final LibraryDescription libraryDescription) {
		return super.create(libraryDescription);
	}
}

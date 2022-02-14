package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

/**
 * Service implementation for storing and retrieving RemoteAPI objects from a
 * {@link RemoteAPIRepository}
 * 
 *
 */
@Transactional
@Service
public class RemoteAPIServiceImpl extends CRUDServiceImpl<Long, RemoteAPI> implements RemoteAPIService {

	RemoteAPIRepository repository;
	
	private RemoteAPIServiceImpl() {
		super(null, null, RemoteAPI.class);
	}

	@Autowired
	public RemoteAPIServiceImpl(RemoteAPIRepository repository, Validator validator)
			throws ConstraintViolationException, EntityExistsException {
		super(repository, validator, RemoteAPI.class);
		this.repository = repository;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public RemoteAPI create(final RemoteAPI remoteAPI) {
		return super.create(remoteAPI);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("permitAll")
	public Page<RemoteAPI> search(Specification<RemoteAPI> specification, int page, int size, Direction order,
			String... sortProperties) {
		return super.search(specification, PageRequest.of(page, size, order, sortProperties));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void delete(Long id) throws EntityNotFoundException {
		super.delete(id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("permitAll")
	public RemoteAPI read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("permitAll")
	public Iterable<RemoteAPI> findAll() {
		return super.findAll();
	}

	@Override
	@PreAuthorize("permitAll")
	public RemoteAPI getRemoteAPIForUrl(String url) {
		return repository.getRemoteAPIForUrl(url);
	}

}

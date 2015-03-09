package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
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

	private RemoteAPIServiceImpl() {
		super(null, null, RemoteAPI.class);
	}

	@Autowired
	public RemoteAPIServiceImpl(RemoteAPIRepository repository, Validator validator)
			throws ConstraintViolationException, EntityExistsException {
		super(repository, validator, RemoteAPI.class);
	}

	@Override
	public RemoteAPI read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	@Override
	public Iterable<RemoteAPI> findAll() {
		return super.findAll();
	}

}

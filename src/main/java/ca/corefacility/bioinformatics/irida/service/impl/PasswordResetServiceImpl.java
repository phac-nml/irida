package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.PasswordReset;
import ca.corefacility.bioinformatics.irida.repositories.PasswordResetRepository;
import ca.corefacility.bioinformatics.irida.service.PasswordResetService;

/**
 * Implementation for managing {@link PasswordReset}
 */
@Service
public class PasswordResetServiceImpl extends CRUDServiceImpl<Long, PasswordReset> implements PasswordResetService {
	private static final Logger logger = LoggerFactory.getLogger(PasswordResetServiceImpl.class);

	/**
	 * A reference to a password reset repository
	 */
	private PasswordResetRepository passwordResetRepository;

	protected PasswordResetServiceImpl() {
		super(null, null, PasswordReset.class);
	}

	@Autowired
	public PasswordResetServiceImpl(PasswordResetRepository passwordResetRepository, Validator validator) {
		super(passwordResetRepository, validator, PasswordReset.class);
		this.passwordResetRepository = passwordResetRepository;
	}

	@Override
	public PasswordReset createForUser(String email) throws EntityExistsException, ConstraintViolationException {
		return null;
	}

	@Override
	public PasswordReset create(@Valid PasswordReset object) throws EntityExistsException, ConstraintViolationException {
		return null;
	}

	@Override
	public PasswordReset read(Long id) throws EntityNotFoundException {
		return null;
	}

	@Override
	public Iterable<PasswordReset> readMultiple(Iterable<Long> idents) {
		return null;
	}

	@Override
	public PasswordReset update(Long id, Map<String, Object> updatedProperties) throws EntityExistsException,
			EntityNotFoundException, ConstraintViolationException, InvalidPropertyException {
		return null;
	}

	@Override
	public void delete(Long id) throws EntityNotFoundException {

	}

	@Override
	public Iterable<PasswordReset> findAll() {
		return null;
	}

	@Override
	public Page<PasswordReset> list(int page, int size, Sort.Direction order, String... sortProperty)
			throws IllegalArgumentException {
		return null;
	}

	@Override
	public Page<PasswordReset> list(int page, int size, Sort.Direction order) {
		return null;
	}

	@Override
	public Boolean exists(Long id) {
		return null;
	}

	@Override
	public long count() {
		return 0;
	}
}

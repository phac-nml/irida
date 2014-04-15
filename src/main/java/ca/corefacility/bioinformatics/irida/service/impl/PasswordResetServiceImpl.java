package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.PasswordReset;
import ca.corefacility.bioinformatics.irida.repositories.PasswordResetRepository;
import ca.corefacility.bioinformatics.irida.service.PasswordResetService;

/**
 * Implementation for managing {@link PasswordReset}
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * 
 */
@Service
public class PasswordResetServiceImpl extends CRUDServiceImpl<String, PasswordReset> implements PasswordResetService {
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
	public PasswordReset create(@Valid PasswordReset passwordReset) {
		// Find existing resets
		PasswordReset pw = passwordResetRepository.findByUser(passwordReset.getUser());

		// Delete them
		if (pw != null) {
			passwordResetRepository.delete(pw);
		}

		return super.create(passwordReset);
	}

	@Override
	public void delete(String key) {
		super.delete(key);
	}

	@Override
	public PasswordReset read(String key) {
		return super.read(key);
	}

	@Override
	public PasswordReset update(String key, Map<String, Object> changes) {
		throw new UnsupportedOperationException("PasswordResets cannot be updated.");
	}
}

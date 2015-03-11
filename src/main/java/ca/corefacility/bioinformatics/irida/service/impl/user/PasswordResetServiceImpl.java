package ca.corefacility.bioinformatics.irida.service.impl.user;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.repositories.user.PasswordResetRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;

/**
 * Implementation for managing {@link PasswordReset}
 * 
 * 
 */
@Service
public class PasswordResetServiceImpl extends CRUDServiceImpl<String, PasswordReset> implements PasswordResetService {

	/**
	 * A reference to a password reset repository
	 */
	private PasswordResetRepository passwordResetRepository;

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

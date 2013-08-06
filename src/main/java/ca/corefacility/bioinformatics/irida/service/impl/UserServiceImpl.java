package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;

/**
 * Implementation of the {@link UserService}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserServiceImpl extends CRUDServiceImpl<Long, User> implements UserService {
	/**
	 * The property name to use for passwords on the {@link User} class.
	 */
	private static final String PASSWORD_PROPERTY = "password";
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	/**
	 * A reference to the user repository used to manage users.
	 */
	private UserRepository userRepository;
	/**
	 * A reference to the password encoder used by the system for storing
	 * passwords.
	 */
	private PasswordEncoder passwordEncoder;

	/**
	 * Constructor, requires a handle on a validator and a repository.
	 * 
	 * @param userRepository
	 *            the repository used to store instances of {@link User}.
	 * @param validator
	 *            the validator used to validate instances of {@link User}.
	 */
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Validator validator) {
		super(userRepository, validator, User.class);
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public User create(User u) {
		Set<ConstraintViolation<User>> violations = validatePassword(u.getPassword());
		if (violations.isEmpty()) {
			String password = u.getPassword();
			u.setPassword(passwordEncoder.encode(password));
			if (u.getSystemRole() == null) {
				u.setSystemRole(new Role("ROLE_USER"));
			}
			return super.create(u);
		}

		throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#uid, 'canUpdateUser')")
	public User update(Long uid, Map<String, Object> properties) {
		if (properties.containsKey(PASSWORD_PROPERTY)) {
			String password = properties.get(PASSWORD_PROPERTY).toString();
			Set<ConstraintViolation<User>> violations = validatePassword(password);
			if (violations.isEmpty()) {
				properties.put(PASSWORD_PROPERTY, passwordEncoder.encode(password));
			} else {
				throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
			}
		}

		return super.update(uid, properties);
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void delete(Long id) {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUserByUsername(String username) throws EntityNotFoundException {
		return userRepository.getUserByUsername(username);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Join<Project, User>> getUsersForProject(Project project) {
		return userRepository.getUsersForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.trace("Loading user with username: [" + username + "].");
		org.springframework.security.core.userdetails.User userDetails = null;
		User u;
		try {
			u = userRepository.getUserByUsername(username);

			userDetails = new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(),
					u.getAuthorities());
		} catch (EntityNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
		return userDetails;
	}

	/**
	 * Validate the password of a {@link User} *before* encoding the password
	 * and passing to super.
	 * 
	 * @param password
	 *            the password to validate.
	 * @return true if valid, false otherwise.
	 */
	private Set<ConstraintViolation<User>> validatePassword(String password) {
		return validator.validateValue(User.class, PASSWORD_PROPERTY, password);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public List<User> getUsersAvailableForProject(Project project) {
		return userRepository.getUsersAvailableForProject(project);
	}
}

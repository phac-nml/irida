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
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation of the {@link UserService}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Transactional
public class UserServiceImpl extends CRUDServiceImpl<Long, User> implements UserService {
	/**
	 * The property name to use for passwords on the {@link User} class.
	 */
	private static final String PASSWORD_PROPERTY = "password";
	/**
	 * The property name to use for expired credentials on the {@link User}
	 * class.
	 */
	private static final String CREDENTIALS_NON_EXPIRED_PROPERTY = "credentialsNonExpired";
	/**
	 * logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	/**
	 * A reference to the user repository used to manage users.
	 */
	private UserRepository userRepository;
	/**
	 * A reference to the project user join repository.
	 */
	private ProjectUserJoinRepository pujRepository;
	/**
	 * A reference to the password encoder used by the system for storing
	 * passwords.
	 */
	private PasswordEncoder passwordEncoder;
	/**
	 * If a user is an administrator, they are permitted to create a user
	 * account with any role. If a user is a manager, then they are only
	 * permitted to create user accounts with a ROLE_USER role.
	 */
	private static final String CREATE_USER_PERMISSIONS = "hasRole('ROLE_ADMIN') or "
			+ "((#u.getSystemRole() == T(ca.corefacility.bioinformatics.irida.model.Role).ROLE_USER) and hasRole('ROLE_MANAGER'))";
	/**
	 * If a user is an administrator, they are permitted to update any user
	 * property. If a manager or user is updating an account, they should not be
	 * permitted to change the role of the user (only administrators can create
	 * users with role other than Role.ROLE_USER).
	 */
	private static final String UPDATE_USER_PERMISSIONS = "hasRole('ROLE_ADMIN') or "
			+ "(!#properties.containsKey('systemRole') and (hasRole('ROLE_MANAGER') or hasPermission(#uid, 'canUpdateUser')))";

	/**
	 * A user is permitted to change their own password if they did not
	 * successfully log in, but the reason for the login failure is that their
	 * credentials are expired. This permission checks to see that the user is
	 * authenticated, or that the principle in the security context has an
	 * expired password.
	 */
	private static final String CHANGE_PASSWORD_PERMISSIONS = "isFullyAuthenticated() or "
			+ "(principal instanceof T(ca.corefacility.bioinformatics.irida.model.User) and !principal.isCredentialsNonExpired())";

	protected UserServiceImpl() {
		super(null, null, User.class);
	}

	/**
	 * Constructor, requires a handle on a validator and a repository.
	 * 
	 * @param userRepository
	 *            the repository used to store instances of {@link User}.
	 * @param validator
	 *            the validator used to validate instances of {@link User}.
	 */
	public UserServiceImpl(UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			PasswordEncoder passwordEncoder, Validator validator) {
		super(userRepository, validator, User.class);
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.pujRepository = pujRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize(CHANGE_PASSWORD_PERMISSIONS)
	public User changePassword(Long userId, String password) {
		Set<ConstraintViolation<User>> violations = validatePassword(password);
		if (violations.isEmpty()) {
			String encodedPassword = passwordEncoder.encode(password);
			return super.update(userId, ImmutableMap.of(PASSWORD_PROPERTY, (Object) encodedPassword,
					CREDENTIALS_NON_EXPIRED_PROPERTY, true));
		}

		throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize(CREATE_USER_PERMISSIONS)
	public User create(User u) {
		Set<ConstraintViolation<User>> violations = validatePassword(u.getPassword());
		if (violations.isEmpty()) {
			// encode the user password
			String password = u.getPassword();
			u.setPassword(passwordEncoder.encode(password));
			return super.create(u);
		}

		throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize(UPDATE_USER_PERMISSIONS)
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
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
	public void delete(Long id) {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserByUsername(String username) throws EntityNotFoundException {
		return userRepository.loadUserByUsername(username);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<Join<Project, User>> getUsersForProject(Project project) {
		return pujRepository.getUsersForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.trace("Loading user with username: [" + username + "].");
		org.springframework.security.core.userdetails.User userDetails = null;
		User u;
		try {
			u = userRepository.loadUserByUsername(username);

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
	@Transactional(readOnly = true)
	public List<User> getUsersAvailableForProject(Project project) {
		return userRepository.getUsersAvailableForProject(project);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<Join<Project, User>> getUsersForProjectByRole(Project project, ProjectRole projectRole) {
		return pujRepository.getUsersForProjectByRole(project, projectRole);
	}

	@Override
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
	public Iterable<User> findAll() {
		return repository.findAll();
	}
}

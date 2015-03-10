package ca.corefacility.bioinformatics.irida.service.impl.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Implementation of the {@link UserService}.
 * 
 */
@Transactional
@Service
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
	 * A reference to the user group join repository.
	 */
	private UserGroupJoinRepository userGroupRepository;

	private static final Pattern USER_CONSTRAINT_PATTERN;

	static {
		String regex = "^USER_(.*)_CONSTRAINT$";
		USER_CONSTRAINT_PATTERN = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Constructor, requires a handle on a validator and a repository.
	 * 
	 * @param userRepository
	 *            the repository used to store instances of {@link User}.
	 * @param validator
	 *            the validator used to validate instances of {@link User}.
	 */
	@Autowired
	public UserServiceImpl(UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			UserGroupJoinRepository userGroupJoinRepository, PasswordEncoder passwordEncoder, Validator validator) {
		super(userRepository, validator, User.class);
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.pujRepository = pujRepository;
		this.userGroupRepository = userGroupJoinRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<User> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
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
	public User create(User u) {
		String password = u.getPassword();
		u.setPassword(passwordEncoder.encode(password));
		try {
			return super.create(u);
		} catch (DataIntegrityViolationException e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				RuntimeException translated = translateConstraintViolationException((org.hibernate.exception.ConstraintViolationException) e
						.getCause());
				throw translated;
			} else {
				// I can't figure out what the problem was, just keep
				// throwing it up.
				throw new DataIntegrityViolationException(
						"Unexpected DataIntegrityViolationException, cause accompanies.", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User update(Long uid, Map<String, Object> properties) {
		if (properties.containsKey(PASSWORD_PROPERTY)) {
			String password = properties.get(PASSWORD_PROPERTY).toString();
			Set<ConstraintViolation<User>> violations = validatePassword(password);
			if (violations.isEmpty()) {
				properties.put(PASSWORD_PROPERTY, passwordEncoder.encode(password));
			} else {
				throw new ConstraintViolationException(violations);
			}
		}

		return super.update(uid, properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserByUsername(String username) throws EntityNotFoundException {
		User u = userRepository.loadUserByUsername(username);
		if (u == null) {
			throw new EntityNotFoundException("User could not be found.");
		}
		return u;
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
	 * {@inheritDoc}
	 */
	@Override
	public User loadUserByEmail(String email) throws EntityNotFoundException {
		logger.trace("Loading user with email " + email);
		User loadUserByEmail = userRepository.loadUserByEmail(email);
		if (loadUserByEmail == null) {
			throw new EntityNotFoundException("User could not be found with that email address.");
		}
		return loadUserByEmail;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<Join<User, Group>> getUsersForGroup(Group g) throws EntityNotFoundException {
		return userGroupRepository.getUsersForGroup(g);
	}

	/**
	 * Translate {@link ConstraintViolationException} errors into an appropriate
	 * {@link EntityExistsException}.
	 * 
	 * @param e
	 *            the exception to translate.
	 * @return the translated exception.
	 */
	private RuntimeException translateConstraintViolationException(
			org.hibernate.exception.ConstraintViolationException e) {
		final EntityExistsException UNABLE_TO_PARSE = new EntityExistsException(
				"Could not create user as a duplicate fields exists; however the duplicate field was not included in "
						+ "the ConstraintViolationException, the original cause is included.", e);
		String constraintName = e.getConstraintName();

		if (StringUtils.isEmpty(constraintName)) {
			return UNABLE_TO_PARSE;
		}
		Matcher matcher = USER_CONSTRAINT_PATTERN.matcher(constraintName);
		if (matcher.groupCount() != 1) {
			throw new IllegalArgumentException("The pattern must have capture groups to parse the constraint name.");
		}

		if (!matcher.find()) {
			return UNABLE_TO_PARSE;
		}
		String fieldName = matcher.group(1).toLowerCase();

		return new EntityExistsException("Could not create user as a duplicate field exists: " + fieldName, fieldName);
	}

}

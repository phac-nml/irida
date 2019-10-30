package ca.corefacility.bioinformatics.irida.service.impl.user;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	private static final String LAST_LOGIN_PROPERTY = "lastLogin";
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

	private static final Pattern USER_CONSTRAINT_PATTERN;
	
	/**
	 * A user is permitted to change their own password if they did not
	 * successfully log in, but the reason for the login failure is that their
	 * credentials are expired. This permission checks to see that the user is
	 * authenticated, or that the principle in the security context has an
	 * expired password.
	 */
	private static final String CHANGE_PASSWORD_PERMISSIONS = "isFullyAuthenticated() or "
			+ "(principal instanceof T(ca.corefacility.bioinformatics.irida.model.user.User) and !principal.isCredentialsNonExpired())";

	/**
	 * If a user is an administrator, they are permitted to create a user
	 * account with any role. If a user is a manager, then they are only
	 * permitted to create user accounts with a ROLE_USER role.
	 */
	private static final String CREATE_USER_PERMISSIONS = "hasRole('ROLE_ADMIN') or "
			+ "((#u.getSystemRole() == T(ca.corefacility.bioinformatics.irida.model.user.Role).ROLE_USER) and hasRole('ROLE_MANAGER'))";

	/**
	 * If a user is an administrator, they are permitted to update any user
	 * property. If a manager or user is updating an account, they should not be
	 * permitted to change the role of the user (only administrators can create
	 * users with role other than Role.ROLE_USER).
	 */
	static final String UPDATE_USER_PERMISSIONS = "hasRole('ROLE_ADMIN') or "
			+ "(!#properties.containsKey('systemRole') and hasPermission(#uid, 'canUpdateUser'))";

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
	 * @param pujRepository
	 *            the project user join repository.
	 * @param passwordEncoder
	 *            the password encoder.
	 */
	@Autowired
	public UserServiceImpl(UserRepository userRepository, ProjectUserJoinRepository pujRepository, PasswordEncoder passwordEncoder, Validator validator) {
		super(userRepository, validator, User.class);
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.pujRepository = pujRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public User read(final Long id) {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Iterable<User> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	public void delete(final Long id) {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
     */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public long count() {
		return super.count();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<User> search(Specification<User> specification, Pageable pageRequest) {
		return super.search(specification, pageRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize(CHANGE_PASSWORD_PERMISSIONS)
	public User changePassword(Long userId, String password) {
		Set<ConstraintViolation<User>> violations = validatePassword(userId, password);
		if (violations.isEmpty()) {
			String encodedPassword = passwordEncoder.encode(password);
			return super.updateFields(userId, ImmutableMap.of(PASSWORD_PROPERTY, (Object) encodedPassword,
					CREDENTIALS_NON_EXPIRED_PROPERTY, true));
		}

		throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize(CREATE_USER_PERMISSIONS)
	public User create(@Valid User u) {
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
	@PreAuthorize(UPDATE_USER_PERMISSIONS)
	@Override
	public User updateFields(Long uid, Map<String, Object> properties)
			throws ConstraintViolationException, EntityExistsException, InvalidPropertyException {
		if (properties.containsKey(PASSWORD_PROPERTY)) {
			String password = properties.get(PASSWORD_PROPERTY).toString();
			Set<ConstraintViolation<User>> violations = validatePassword(uid, password);
			if (violations.isEmpty()) {
				properties.put(PASSWORD_PROPERTY, passwordEncoder.encode(password));
			} else {
				throw new ConstraintViolationException(violations);
			}
		}
		if (properties.containsKey(LAST_LOGIN_PROPERTY)) {
			throw new IllegalArgumentException("Cannot update last login property");
		}

		return super.updateFields(uid, properties);
	}

	/**
	 * Throws an {@link UnsupportedOperationException} telling user to use
	 * {@link UserServiceImpl#updateFields(Long, Map)} instead. They should use
	 * the other method so that they cannot update a password without explicitly
	 * trying to do so.
	 */
	@Override
	public User update(User object) {
		throw new UnsupportedOperationException("Update is not supported for UserService.  Use updateFields instead.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("permitAll")
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
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Collection<Join<Project, User>> getUsersForProject(Project project) {
		return pujRepository.getUsersForProject(project);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Long countUsersForProject(Project project) {
		return pujRepository.countUsersForProject(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("permitAll")
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
	@PreAuthorize("permitAll")
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
	 * and passing to super.  This will check both password structure and whether a password has been reused
	 *
	 * @param userId   the ID of the user to check for old passwords.
	 * @param password the password to validate.
	 * @return true if valid, false otherwise.
	 */
	private Set<ConstraintViolation<User>> validatePassword(Long userId, String password) {
		//check revisions for reused passwords
		Revisions<Integer, User> revisions = repository.findRevisions(userId);

		Set<String> oldPasswords = revisions.getContent().stream().map(r -> r.getEntity().getPassword())
				.collect(Collectors.toSet());

		for (String oldPassword : oldPasswords) {
			if (passwordEncoder.matches(password, oldPassword)) {
				throw new PasswordReusedException("Password has already been used.");
			}
		}

		// if no reused passwords, check other validation
		return validator.validateValue(User.class, PASSWORD_PROPERTY, password);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<User> getUsersAvailableForProject(final Project project, final String term) {
		return userRepository.getUsersAvailableForProject(project, term);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Collection<Join<Project, User>> getUsersForProjectByRole(Project project, ProjectRole projectRole) {
		return pujRepository.getUsersForProjectByRole(project, projectRole);
	}

	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Override
	public List<User> getUsersWithEmailSubscriptions() {
		return pujRepository.getUsersWithSubscriptions();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#user, 'canUpdateUser')")
	public ProjectUserJoin updateEmailSubscription(User user, Project project, boolean subscribed) {
		ProjectUserJoin projectJoinForUser = pujRepository.getProjectJoinForUser(project, user);
		projectJoinForUser.setEmailSubscription(subscribed);

		return pujRepository.save(projectJoinForUser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Page<Join<Project, User>> searchUsersForProject(final Project project, final String search, final int page,
			final int size, final Sort sort) {
		return pujRepository.getUsersForProject(project, search, PageRequest.of(page, size, sort));
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

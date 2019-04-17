package ca.corefacility.bioinformatics.irida.service.impl.user;

import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityRevisionDeletedException;
import ca.corefacility.bioinformatics.irida.exceptions.UserGroupWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin.UserGroupRole;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

/**
 * Implementation of {@link UserGroupService}.
 *
 */
@Service
public class UserGroupServiceImpl extends CRUDServiceImpl<Long, UserGroup> implements UserGroupService {

	private final UserGroupJoinRepository userGroupJoinRepository;
	private final UserRepository userRepository;
	private final UserGroupProjectJoinRepository userGroupProjectJoinRepository;
	private final UserGroupRepository userGroupRepository;

	/**
	 * Create a new {@link UserGroupServiceImpl}.
	 * 
	 * @param userGroupRepository
	 *            the {@link UserGroupRepository}
	 * @param userGroupJoinRepository
	 *            the {@link UserGroupJoinRepository}
	 * @param userRepository
	 *            the {@link UserRepository}
	 * @param userGroupProjectJoinRepository
	 *            The {@link UserGroupProjectJoinRepository}
	 * @param validator
	 *            the {@link Validator}
	 */
	@Autowired
	public UserGroupServiceImpl(final UserGroupRepository userGroupRepository,
			final UserGroupJoinRepository userGroupJoinRepository, final UserRepository userRepository,
			final UserGroupProjectJoinRepository userGroupProjectJoinRepository, final Validator validator) {
		super(userGroupRepository, validator, UserGroup.class);
		this.userGroupRepository = userGroupRepository;
		this.userGroupJoinRepository = userGroupJoinRepository;
		this.userRepository = userRepository;
		this.userGroupProjectJoinRepository = userGroupProjectJoinRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional
	public UserGroup create(UserGroup object) throws EntityExistsException, ConstraintViolationException {
		final UserGroup ug = super.create(object);
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final User currentUser = userRepository.loadUserByUsername(auth.getName());
		addUserToGroup(currentUser, ug, UserGroupRole.GROUP_OWNER);
		return ug;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public UserGroup read(Long id) throws EntityNotFoundException {
		return super.read(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Iterable<UserGroup> readMultiple(Iterable<Long> idents) {
		return super.readMultiple(idents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canUpdateUserGroup')")
	public void delete(Long id) throws EntityNotFoundException {
		super.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Iterable<UserGroup> findAll() {
		return super.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<UserGroup> list(int page, int size, Direction order, String... sortProperty)
			throws IllegalArgumentException {
		return super.list(page, size, order, sortProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<UserGroup> list(int page, int size, Direction order) {
		return super.list(page, size, order);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Boolean exists(Long id) {
		return super.exists(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public long count() {
		return super.count();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<UserGroup> search(Specification<UserGroup> specification, int page, int size, Direction order,
			String... sortProperties) {
		return super.search(specification, page, size, order, sortProperties);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@PreAuthorize("hasPermission(#object, 'canUpdateUserGroup')")
	@Override
	public UserGroup update(UserGroup object) {
		return super.update(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Revisions<Integer, UserGroup> findRevisions(Long id) throws EntityRevisionDeletedException {
		return super.findRevisions(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<Revision<Integer, UserGroup>> findRevisions(Long id, Pageable pageable)
			throws EntityRevisionDeletedException {
		return super.findRevisions(id, pageable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Collection<UserGroupJoin> getUsersForGroup(final UserGroup group) {
		return userGroupJoinRepository.findUsersInGroup(group);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#group, 'canUpdateUserGroup')")
	public Collection<UserGroupProjectJoin> getProjectsWithUserGroup(final UserGroup group) {
		return userGroupProjectJoinRepository.findProjectsByUserGroup(group);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#userGroup, 'canUpdateUserGroup')")
	public UserGroupJoin addUserToGroup(final User user, final UserGroup userGroup, final UserGroupRole role) {
		final UserGroupJoin join = new UserGroupJoin(user, userGroup, role);

		return userGroupJoinRepository.save(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#userGroup, 'canUpdateUserGroup')")
	public UserGroupJoin changeUserGroupRole(final User user, final UserGroup userGroup, final UserGroupRole role)
			throws UserGroupWithoutOwnerException {
		final UserGroupJoin join = userGroupJoinRepository.findOne(findUserGroupJoin(user, userGroup));

		if (!allowRoleChange(userGroup, join.getRole())) {
			throw new UserGroupWithoutOwnerException(
					"Cannot change this user's group role because it would leave the group without an owner.");
		}

		join.setRole(role);
		return userGroupJoinRepository.save(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#userGroup, 'canUpdateUserGroup')")
	public void removeUserFromGroup(final User user, final UserGroup userGroup) throws UserGroupWithoutOwnerException {
		final UserGroupJoin join = userGroupJoinRepository.findOne(findUserGroupJoin(user, userGroup));

		if (!allowRoleChange(userGroup, join.getRole())) {
			throw new UserGroupWithoutOwnerException(
					"Cannot remove this user from the group because it would leave the group without an owner.");
		}

		userGroupJoinRepository.delete(join);
	}

	/**
	 * Check to see if changing the role will change the number of group owners
	 * to 0.
	 * 
	 * @param userGroup
	 *            the group to check
	 * @param role
	 *            the role you're going to be changing from
	 * @return false if the role change results in no group owners, true
	 *         otherwise
	 */
	public boolean allowRoleChange(final UserGroup userGroup, final UserGroupRole role) {
		if (!role.equals(UserGroupRole.GROUP_OWNER)) {
			// the role that we're changing from is not GROUP_OWNER (i.e., we're
			// probably making this person a GROUP_OWNER) so this transition is
			// allowed.
			return true;
		}

		// get the set of group owners
		final List<UserGroupJoin> users = userGroupJoinRepository
				.findAll(filterUserGroupJoinByRole(UserGroupRole.GROUP_OWNER));

		// if there are at least 2 group owners, then it doesn't matter what
		// we're changing the role to.
		return users.size() >= 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<UserGroupJoin> filterUsersByUsername(final String username, final UserGroup userGroup, int page,
			int size, Sort sort) {
		return userGroupJoinRepository.findAll(filterUserGroupJoinByUsername(username, userGroup),
				new PageRequest(page, size, sort));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Collection<User> getUsersNotInGroup(final UserGroup userGroup) {
		return userGroupJoinRepository.findUsersNotInGroup(userGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public Page<UserGroupProjectJoin> getUserGroupsForProject(final String searchName, final Project project,
			final int page, final int size, final Sort sort) {
		return userGroupProjectJoinRepository.findAll(filterUserGroupProjectJoinByProject(searchName, project),
				new PageRequest(page, size, sort));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#project, 'canReadProject')")
	public List<UserGroup> getUserGroupsNotOnProject(final Project project, final String search) {
		return userGroupRepository.findUserGroupsNotOnProject(project, search);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Page<UserGroup> search(Specification<UserGroup> specification, PageRequest pageRequest) {
		return super.search(specification, pageRequest);
	}

	/**
	 * A convenience specification to get a {@link UserGroupJoin} from a
	 * {@link User} and {@link UserGroup}.
	 * 
	 * @param user
	 *            the user
	 * @param userGroup
	 *            the group
	 * @return a specification for the filter
	 */
	private static final Specification<UserGroupJoin> findUserGroupJoin(final User user, final UserGroup userGroup) {
		return new Specification<UserGroupJoin>() {
			@Override
			public Predicate toPredicate(Root<UserGroupJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("user"), user), cb.equal(root.get("group"), userGroup));
			}
		};
	}

	/**
	 * A convenience specification to filter {@link UserGroupJoin} in a
	 * {@link UserGroup} by the username.
	 * 
	 * @param username
	 *            the username to filter on
	 * @param userGroup
	 *            the group to filter
	 * @return a specification for the filter
	 */
	private static final Specification<UserGroupJoin> filterUserGroupJoinByUsername(final String username,
			final UserGroup userGroup) {
		return new Specification<UserGroupJoin>() {
			@Override
			public Predicate toPredicate(final Root<UserGroupJoin> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {
				return cb.and(cb.like(root.get("user").get("username"), "%" + username + "%"),
						cb.equal(root.get("group"), userGroup));
			}
		};
	}

	/**
	 * A convenience specification to filter {@link UserGroupProjectJoin} by
	 * group name and project.
	 * 
	 * @param searchName
	 *            the name to search on
	 * @param p
	 *            the project to get joins for
	 * @return a specification for the filter
	 */
	private static final Specification<UserGroupProjectJoin> filterUserGroupProjectJoinByProject(
			final String searchName, final Project p) {
		return new Specification<UserGroupProjectJoin>() {
			@Override
			public Predicate toPredicate(final Root<UserGroupProjectJoin> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {
				return cb.and(cb.like(root.get("userGroup").get("name"), "%" + searchName + "%"),
						cb.equal(root.get("project"), p));
			}
		};
	}

	/**
	 * A convenience specification to filter {@link UserGroupJoin} by role.
	 * 
	 * @param role
	 *            the role type to filter on
	 * @return a specification for the filter.
	 */
	private static final Specification<UserGroupJoin> filterUserGroupJoinByRole(final UserGroupRole role) {
		return new Specification<UserGroupJoin>() {
			@Override
			public Predicate toPredicate(final Root<UserGroupJoin> root, final CriteriaQuery<?> query,
					final CriteriaBuilder cb) {
				return cb.equal(root.get("role"), role);
			}
		};
	}
}

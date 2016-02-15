package ca.corefacility.bioinformatics.irida.service.impl.user;

import java.util.Collection;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityRevisionDeletedException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin.UserGroupRole;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

/**
 * Implementation of {@link UserGroupService}.
 *
 */
@Service
public class UserGroupServiceImpl extends CRUDServiceImpl<Long, UserGroup> implements UserGroupService {

	private final UserGroupJoinRepository userGroupJoinRepository;

	/**
	 * Create a new {@link UserGroupServiceImpl}.
	 * 
	 * @param userGroupRepository
	 *            the {@link UserGroupRepository}
	 * @param validator
	 *            the {@link Validator}
	 */
	@Autowired
	public UserGroupServiceImpl(final UserGroupRepository userGroupRepository,
			final UserGroupJoinRepository userGroupJoinRepository, final Validator validator) {
		super(userGroupRepository, validator, UserGroup.class);
		this.userGroupJoinRepository = userGroupJoinRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	public UserGroup create(UserGroup object) throws EntityExistsException, ConstraintViolationException {
		return super.create(object);
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
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public UserGroup update(Long id, Map<String, Object> updatedProperties) throws EntityExistsException,
			EntityNotFoundException, ConstraintViolationException, InvalidPropertyException {
		return super.update(id, updatedProperties);
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
	@PreAuthorize("hasRole('ROLE_USER'")
	public Collection<UserGroupJoin> getUsersForGroup(final UserGroup group) {
		return userGroupJoinRepository.findUsersInGroup(group);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public UserGroupJoin addUserToGroup(final User user, final UserGroup userGroup, final UserGroupRole role) {
		final UserGroupJoin join = new UserGroupJoin(user, userGroup, role);

		return userGroupJoinRepository.save(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public UserGroupJoin changeUserGroupRole(final User user, final UserGroup userGroup, final UserGroupRole role) {
		final UserGroupJoin join = userGroupJoinRepository.findOne(findUserGroupJoin(user, userGroup));

		join.setRole(role);
		return userGroupJoinRepository.save(join);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void removeUserFromGroup(final User user, final UserGroup userGroup) {
		final UserGroupJoin join = userGroupJoinRepository.findOne(findUserGroupJoin(user, userGroup));
		userGroupJoinRepository.delete(join);
	}

	/**
	 * A convenience specification to get a {@link UserGroupJoin} from a
	 * {@link User} and {@link UserGroup}.
	 * 
	 * @param user
	 *            the user
	 * @param userGroup
	 *            the group
	 * @return the relationship for those objects.
	 */
	private static final Specification<UserGroupJoin> findUserGroupJoin(final User user, final UserGroup userGroup) {
		return new Specification<UserGroupJoin>() {
			@Override
			public Predicate toPredicate(Root<UserGroupJoin> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.and(cb.equal(root.get("user"), user), cb.equal(root.get("group"), userGroup));
			}
		};
	}
}

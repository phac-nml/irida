package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.UserGroupWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserGroupSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.*;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Service class for the UI for handling {@link UserGroup}s
 */
@Component
public class UIUserGroupsService {
	private final UserGroupService userGroupService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public UIUserGroupsService(UserGroupService userGroupService, UserService userService,
			MessageSource messageSource) {
		this.userGroupService = userGroupService;
		this.userService = userService;
		this.messageSource = messageSource;
	}

	/**
	 * Gat a paged list of user groups
	 *
	 * @param request details about the current table page
	 * @return {@link TableResponse} for the current page of user groups
	 */
	public TableResponse<UserGroupTableModel> getUserGroups(TableRequest request) {
		Page<UserGroup> pagedGroups = userGroupService.search(
				UserGroupSpecification.searchUserGroup(request.getSearch()),
				PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort()));
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		boolean isAdmin = user.getSystemRole()
				.equals(Role.ROLE_ADMIN);
		List<UserGroupTableModel> groups = pagedGroups.getContent()
				.stream()
				.map(group -> new UserGroupTableModel(group, isAdmin || isGroupOwner(user, group)))
				.collect(Collectors.toList());
		return new TableResponse<>(groups, pagedGroups.getTotalElements());
	}

	/**
	 * Delete a specific user group
	 *
	 * @param id     Identifier for the user group to delete
	 * @param locale Current users locale
	 * @return Message to user about what happened
	 */
	public String deleteUserGroup(Long id, Locale locale) {
		UserGroup group = userGroupService.read(id);
		userGroupService.delete(id);
		return messageSource.getMessage("server.usergroups.delete-success", new Object[] { group.getLabel() }, locale);
	}

	/**
	 * Get details about a specific user group
	 *
	 * @param groupId identifier for a {@link UserGroup}
	 * @return {@link UserGroupDetails}
	 */
	public UserGroupDetails getUserGroupDetails(Long groupId) {
		UserGroup group = userGroupService.read(groupId);
		Collection<UserGroupJoin> groupUsers = userGroupService.getUsersForGroup(group);
		List<UserGroupMember> members = groupUsers.stream()
				.map(UserGroupMember::new)
				.collect(Collectors.toList());

		/*
		Determine if the current user can manage this group
		 */
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		boolean isAdmin = user.getSystemRole()
				.equals(Role.ROLE_ADMIN);

		return new UserGroupDetails(group, members, isAdmin || isGroupOwner(user, group));
	}

	/**
	 * Update user group details
	 *
	 * @param groupId identifier for an {@link UserGroup} to update
	 * @param update  details about which field and value to update
	 */
	@Transactional
	public void updateUserGroupDetails(Long groupId, FieldUpdate update) {
		UserGroup group = userGroupService.read(groupId);
		switch (update.getField()) {
		case "name":
			group.setName(update.getValue());
			break;
		case "description":
			group.setDescription(update.getValue());
			break;
		}
		userGroupService.update(group);
	}

	/**
	 * Get a list of all user group roles with their translations
	 *
	 * @param locale current users {@link Locale}
	 * @return list of {@link UserGroupRole}
	 */
	public List<UserGroupRole> getUserGroupRoles(Locale locale) {
		final String OWNER = UserGroupJoin.UserGroupRole.GROUP_OWNER.toString();
		final String MEMBER = UserGroupJoin.UserGroupRole.GROUP_MEMBER.toString();
		return ImmutableList.of(new UserGroupRole(OWNER,
						messageSource.getMessage("server.usergroups.GROUP_OWNER", new Object[] {}, locale)),
				new UserGroupRole(MEMBER,
						messageSource.getMessage("server.usergroups.GROUP_MEMBER", new Object[] {}, locale)));
	}

	/**
	 * Get a list of system users who are not on the project yet.
	 *
	 * @param groupId identifier for a {@link UserGroup}
	 * @param query   used to search for a specific user
	 * @return list of {@link User} that can be added to the project
	 */
	public List<User> getAvailableUsersForUserGroup(Long groupId, String query) {
		UserGroup group = userGroupService.read(groupId);
		return userGroupService.getUsersNotInGroup(group, query);
	}

	/**
	 * Add a new member to the user group
	 *
	 * @param groupId identifier for the {@link UserGroup}
	 * @param userId identifier for the {@link User}
	 * @param role   role to assign to the user
	 * @param locale  current users {@link Locale}
	 * @return message to the user about the status of this request
	 */
	@Transactional
	public String addMemberToUserGroup(Long groupId, Long userId, String role, Locale locale) {
		UserGroup group = userGroupService.read(groupId);
		User user = userService.read(userId);
		UserGroupJoin.UserGroupRole groupRole = UserGroupJoin.UserGroupRole.fromString(role);
		userGroupService.addUserToGroup(user, group, groupRole);
		return messageSource.getMessage("server.usergroups.add-member", new Object[] { user.getLabel() }, locale);
	}

	/**
	 * Update a users role on a project
	 *
	 * @param groupId identifier for a {@link UserGroup}
	 * @param userId  identifier for a {@link User}
	 * @param role    role to update the user to
	 * @param locale  Current users {@link Locale}
	 * @return Message to user about the result of the update
	 * @throws UserGroupWithoutOwnerException thrown if changing the users role would result in the user group not having an owner
	 */
	public String updateUserRoleOnUserGroup(Long groupId, Long userId, String role, Locale locale)
			throws UserGroupWithoutOwnerException {
		UserGroup group = userGroupService.read(groupId);
		User user = userService.read(userId);
		UserGroupJoin.UserGroupRole userGroupRole = UserGroupJoin.UserGroupRole.fromString(role);
		String roleTranslated = messageSource.getMessage("server.usergroups." + role, new Object[] {}, locale);

		try {
			userGroupService.changeUserGroupRole(user, group, userGroupRole);
			return messageSource.getMessage("server.usergroups.role-success", new Object[] { user.getLabel(), roleTranslated },
					locale);
		} catch (UserGroupWithoutOwnerException e) {
			throw new UserGroupWithoutOwnerException(
					messageSource.getMessage("server.usergroups.role-error", new Object[] { user.getLabel() }, locale));
		}
	}

	/**
	 * Remove a user from an user group
	 *
	 * @param groupId identifier for a {@link UserGroup}
	 * @param userId  identifier for a {@link User}
	 * @param locale  current users {@link Locale}
	 * @return Message to user about the result of removing the user
	 * @throws UserGroupWithoutOwnerException thrown if removing the user would result in the user group not having an owner
	 */
	public String removeMemberFromUserGroup(Long groupId, Long userId, Locale locale)
			throws UserGroupWithoutOwnerException {
		UserGroup group = userGroupService.read(groupId);
		User user = userService.read(userId);
		try {
			userGroupService.removeUserFromGroup(user, group);
			return messageSource.getMessage("server.usergroups.remove-member.success", new Object[] { user.getLabel() },
					locale);
		} catch (UserGroupWithoutOwnerException e) {
			throw new UserGroupWithoutOwnerException(
					messageSource.getMessage("server.usergroups.remove-member.error", new Object[] { user.getLabel() },
							locale));
		}
	}

	/**
	 * Gets a list of projects that are on a user group
	 *
	 * @param groupId identifier for an {@link UserGroup}
	 * @param locale  current users {@link Locale}
	 * @return list of {@link UserGroupProjectTableModel}
	 */
	public List<UserGroupProjectTableModel> getProjectsForUserGroup(Long groupId, Locale locale) {
		UserGroup group = userGroupService.read(groupId);
		Collection<UserGroupProjectJoin> joins = userGroupService.getProjectsWithUserGroup(group);
		return joins.stream()
				.map(join -> {
					ProjectRole role = join.getProjectRole();
					return new UserGroupProjectTableModel(join,
							messageSource.getMessage("projectRole." + role.toString(), new Object[] {}, locale));
				})
				.collect(Collectors.toList());
	}

	/**
	 * Crate a new {@link UserGroup}
	 *
	 * @param userGroup {@link UserGroup} name and description
	 * @param locale    current users {@link Locale}
	 * @return the identifier for the new user groups
	 * @throws UIConstraintViolationException if one of the constraint violations for a {@link UserGroup} is broken
	 */
	public Long createNewUserGroup(UserGroup userGroup, Locale locale) throws UIConstraintViolationException {
		try {
			UserGroup group = userGroupService.create(userGroup);
			return group.getId();
		} catch (Exception e) {
			throw new UIConstraintViolationException(ImmutableMap.of("name",
					messageSource.getMessage("server.usergroups.create.constraint.name",
							new Object[] { userGroup.getLabel() }, locale)));
		}
	}

	/**
	 * Determine if the current user is the owner of the current group.
	 *
	 * @param user  currently logged in user
	 * @param group {@link UserGroup} to see if user is the owner
	 * @return {@link Boolean} true if the owner of the group
	 */
	private boolean isGroupOwner(User user, UserGroup group) {
		Collection<UserGroupJoin> groupUsers = userGroupService.getUsersForGroup(group);
		UserGroupJoin currentUserJoin = groupUsers.stream()
				.filter(x -> x.getSubject()
						.equals(user))
				.findAny()
				.orElse(null);
		if (currentUserJoin != null) {
			return currentUserJoin.getRole()
					.equals(UserGroupJoin.UserGroupRole.GROUP_OWNER);
		}
		return false;
	}
}

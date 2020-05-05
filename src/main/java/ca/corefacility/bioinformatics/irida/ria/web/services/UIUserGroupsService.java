package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.UserGroupWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserGroupSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.*;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;

/**
 * Service class for the UI for handling {@link UserGroup}s
 */
@Component
public class UIUserGroupsService {
	private UserGroupService userGroupService;
	private UserService userService;
	private MessageSource messageSource;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
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

	public void updateGroupDetails(Long groupId, FieldUpdate update) {
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

	public List<UserGroupRole> getUserGroupRoles(Locale locale) {
		final String OWNER = UserGroupJoin.UserGroupRole.GROUP_OWNER.toString();
		final String MEMBER = UserGroupJoin.UserGroupRole.GROUP_MEMBER.toString();
		return ImmutableList.of(new UserGroupRole(OWNER,
						messageSource.getMessage("server.usergroups.GROUP_OWNER", new Object[] {}, locale)),
				new UserGroupRole(MEMBER,
						messageSource.getMessage("server.usergroups.GROUP_MEMBER", new Object[] {}, locale)));
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

	public String updateUserRoleOnUserGroup(Long groupId, Long userId, String role, Locale locale)
			throws UserGroupWithoutOwnerException {
		UserGroup group = userGroupService.read(groupId);
		User user = userService.read(userId);
		UserGroupJoin.UserGroupRole userGroupRole = UserGroupJoin.UserGroupRole.fromString(role);

		try {
			userGroupService.changeUserGroupRole(user, group, userGroupRole);
			return "THIS IS GREAT!";
		} catch (UserGroupWithoutOwnerException e) {
			throw new UserGroupWithoutOwnerException("HEY!  This needs a manager!!!!");
		}
	}
}

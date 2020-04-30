package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserGroupSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserGroupTableModel;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

/**
 * Service class for the UI for handling user groups
 */
@Component
public class UIUserGroupService {
	private final UserGroupService groupService;
	private final MessageSource messageSource;

	@Autowired
	public UIUserGroupService(UserGroupService userGroupService, MessageSource messageSource) {
		this.groupService = userGroupService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a paged list of {@link UserGroup}s
	 *
	 * @param request {@link TableRequest} defining the filters and sort of the list
	 * @return {@link TableResponse}
	 */
	public TableResponse<UserGroupTableModel> getPagedUserGroups(TableRequest request) {
		Page<UserGroup> groups = groupService.search(UserGroupSpecification.searchUserGroup(request.getSearch()),
				PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort()));

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();

		List<UserGroupTableModel> models = groups.getContent()
				.stream()
				.map(group -> createUserGroupTableModel(user, group))
				.collect(Collectors.toList());
		return new TableResponse<>(models, groups.getTotalElements());
	}

	/**
	 * Delete a {@link UserGroup}
	 *
	 * @param groupId identifier for a {@link UserGroup} to delete
	 * @param locale  current users {@link Locale}
	 * @return message to user about the result of the deletion
	 */
	public String deleteUserGroup(Long groupId, Locale locale) {
		UserGroup group = groupService.read(groupId);
		groupService.delete(groupId);
		return messageSource.getMessage("server.UserGroups.remove", new Object[] { group.getLabel() }, locale);
	}

	/**
	 * Create all the information to be passed to the UI about a {@link UserGroup}
	 *
	 * @param user  currently logged in user
	 * @param group {@link UserGroup} to get information about
	 * @return {@link UserGroupTableModel}
	 */
	private UserGroupTableModel createUserGroupTableModel(User user, UserGroup group) {
		boolean canManage = canManageUserGroup(user, group);
		int size = groupService.getUsersForGroup(group)
				.size();
		return new UserGroupTableModel(group, size, canManage);
	}

	/**
	 * Determine if the current user can manage the {@link UserGroup}
	 *
	 * @param user      currently logged in user
	 * @param userGroup {@link UserGroup}
	 * @return boolean if the use can manage the user group
	 */
	private boolean canManageUserGroup(User user, UserGroup userGroup) {
		if (user.getSystemRole()
				.equals(Role.ROLE_ADMIN)) {
			return true;
		}
		Collection<UserGroupJoin> groups = groupService.getUsersForGroup(userGroup);
		Optional<UserGroupJoin> currentUserGroups = groups.stream()
				.filter(userGroupJoin -> userGroupJoin.getSubject()
						.equals(user))
				.findFirst();
		return currentUserGroups.map(userGroupJoin -> userGroupJoin.getRole()
				.equals(UserGroupJoin.UserGroupRole.GROUP_OWNER))
				.orElse(false);
	}
}

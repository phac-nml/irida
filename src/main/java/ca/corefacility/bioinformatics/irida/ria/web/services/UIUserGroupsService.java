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

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserGroupSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

@Component
public class UIUserGroupsService {
	private UserGroupService userGroupService;
	private MessageSource messageSource;

	@Autowired
	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

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

	public String deleteUserGroup(Long id, Locale locale) {
		UserGroup group = userGroupService.read(id);
		userGroupService.delete(id);
		return messageSource.getMessage("server.usergroups.delete-success", new Object[] { group.getLabel() }, locale);
	}

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

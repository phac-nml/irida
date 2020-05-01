package ca.corefacility.bioinformatics.irida.ria.web.ajax.users;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserGroupSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;

@RestController
@RequestMapping("/ajax/user-groups")
public class UserGroupsAjaxController {
	private final UserGroupService userGroupService;

	public UserGroupsAjaxController(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	@RequestMapping("/list")
	public ResponseEntity<TableResponse<UserGroupTableModel>> getUserGroups(@RequestBody TableRequest request) {
		Page<UserGroup> pagedGroups = userGroupService.search(
				UserGroupSpecification.searchUserGroup(request.getSearch()),
				PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort()));
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		boolean isAdmin = user.getSystemRole()
				.equals(Role.ROLE_ADMIN);
		List<UserGroupTableModel> models = pagedGroups.getContent()
				.stream()
				.map(group -> new UserGroupTableModel(group, isAdmin || isGroupOwner(user, group)))
				.collect(Collectors.toList());
		return ResponseEntity.ok(new TableResponse<>(models, pagedGroups.getTotalElements()));
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

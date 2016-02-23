package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin.UserGroupRole;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserGroupSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesUtils;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@Controller
@RequestMapping(value = "/groups")
public class GroupsController {

	private static final Logger logger = LoggerFactory.getLogger(GroupsController.class);
	private static final String GROUPS_LIST = "groups/list";
	private static final String GROUPS_CREATE = "groups/create";
	private static final String GROUP_DETAILS = "groups/details";

	private final UserGroupService userGroupService;
	private final UserService userService;
	private final MessageSource messageSource;

	@Autowired
	public GroupsController(final UserGroupService userGroupService, final UserService userService,
			final MessageSource messageSource) {
		this.userGroupService = userGroupService;
		this.messageSource = messageSource;
		this.userService = userService;
	}

	@RequestMapping
	public String getIndex() {
		return GROUPS_LIST;
	}

	@RequestMapping("/create")
	public String getCreatePage() {
		return GROUPS_CREATE;
	}

	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public String createGroup(final @ModelAttribute UserGroup userGroup, final Model model, final Locale locale) {
		logger.debug("Creating group: [ " + userGroup + "]");
		final Map<String, String> errors = new HashMap<>();
		String forward = GROUPS_LIST;

		try {
			userGroupService.create(userGroup);
		} catch (final ConstraintViolationException e) {
			forward = GROUPS_CREATE;
			for (final ConstraintViolation<?> v : e.getConstraintViolations()) {
				errors.put(v.getPropertyPath().toString(), v.getMessage());
			}
		} catch (final EntityExistsException | DataIntegrityViolationException e) {
			forward = GROUPS_CREATE;
			errors.put("name", messageSource.getMessage("group.name.exists", null, locale));
		}

		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);
			model.addAttribute("given_name", userGroup.getName());
		}

		return forward;
	}

	@RequestMapping("/ajax/list")
	public @ResponseBody DatatablesResponse<UserGroup> getGroups(final @DatatablesParams DatatablesCriterias criteria) {
		final int currentPage = DatatablesUtils.getCurrentPage(criteria);
		final Map<String, Object> sortProperties = DatatablesUtils.getSortProperties(criteria);
		final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
		String sortName = sortProperties.get("sort_string").toString();
		if (sortName.equals("identifier")) {
			sortName = "id";
		}

		final String searchString = criteria.getSearch();
		final Page<UserGroup> groups = userGroupService.search(UserGroupSpecification.searchUserGroup(searchString),
				currentPage, criteria.getLength(), direction, sortName);
		final DataSet<UserGroup> groupsDataSet = new DataSet<>(groups.getContent(), groups.getTotalElements(),
				groups.getTotalElements());
		return DatatablesResponse.build(groupsDataSet, criteria);
	}

	@RequestMapping("/{userGroupId}")
	public String getDetailsPage(final @PathVariable Long userGroupId, final Principal principal, final Model model) {
		final UserGroup group = userGroupService.read(userGroupId);
		final Collection<UserGroupJoin> groupUsers = userGroupService.getUsersForGroup(group);
		final User currentUser = userService.getUserByUsername(principal.getName());
		final boolean isOwner;
		final Optional<UserGroupJoin> currentUserGroup = groupUsers.stream()
				.filter(j -> j.getSubject().equals(currentUser)).findAny();
		if (currentUserGroup.isPresent()) {
			final UserGroupJoin j = currentUserGroup.get();
			isOwner = j.getRole().equals(UserGroupRole.GROUP_OWNER);
		} else {
			isOwner = false;
		}

		model.addAttribute("group", group);
		model.addAttribute("isAdmin", currentUser.getSystemRole().equals(Role.ROLE_ADMIN));
		model.addAttribute("isOwner", isOwner);
		model.addAttribute("users", groupUsers);
		model.addAttribute("groupRoles", UserGroupRole.values());

		return GROUP_DETAILS;
	}

	@RequestMapping("/{userGroupId}/ajax/list")
	public @ResponseBody DatatablesResponse<UserGroupJoin> getGroupUsers(
			final @DatatablesParams DatatablesCriterias criteria, final @PathVariable Long userGroupId) {
		final UserGroup group = userGroupService.read(userGroupId);
		final int currentPage = DatatablesUtils.getCurrentPage(criteria);
		final Map<String, Object> sortProperties = DatatablesUtils.getSortProperties(criteria);
		final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
		String sortName = sortProperties.get("sort_string").toString();

		if (sortName.startsWith("subject")) {
			sortName = "user";
		}

		final String usernameFilter = criteria.getSearch();
		final Page<UserGroupJoin> groups = userGroupService.filterUsersByUsername(usernameFilter, group, currentPage,
				criteria.getLength(), direction, sortName);
		final DataSet<UserGroupJoin> groupDataSet = new DataSet<>(groups.getContent(), groups.getTotalElements(),
				groups.getTotalElements());

		return DatatablesResponse.build(groupDataSet, criteria);
	}

	@RequestMapping("/{userGroupId}/ajax/availablemembers")
	public @ResponseBody Collection<User> getUsersNotInGroup(final @PathVariable Long userGroupId,
			final @RequestParam String term) {
		final UserGroup group = userGroupService.read(userGroupId);
		logger.debug("Loading users not in group [" + userGroupId + "]");
		final Collection<User> usersNotInGroup = userGroupService.getUsersNotInGroup(group);
		return usersNotInGroup.stream().filter(u -> u.getLabel().toLowerCase().contains(term))
				.collect(Collectors.toList());
	}

	@RequestMapping(path = "/{userGroupId}/members", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> addUserToGroup(final @PathVariable Long userGroupId, @RequestParam Long userId,
			@RequestParam String groupRole, Locale locale) {
		final User user = userService.read(userId);
		final UserGroup group = userGroupService.read(userGroupId);
		final UserGroupRole role = UserGroupRole.valueOf(groupRole);
		userGroupService.addUserToGroup(user, group, role);
		return ImmutableMap.of("result", messageSource.getMessage("group.users.add.notification.success", new Object[] { user.getLabel() },
				locale));
	}
}

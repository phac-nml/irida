package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import com.google.common.collect.ImmutableList;
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

/**
 * Controller for interacting with {@link UserGroup}.
 */
@Controller
@RequestMapping(value = "/groups")
public class GroupsController {

	private static final Logger logger = LoggerFactory.getLogger(GroupsController.class);
	private static final String GROUPS_LIST = "groups/list";
	private static final String GROUPS_CREATE = "groups/create";
	private static final String GROUPS_EDIT = "groups/edit";
	private static final String GROUP_DETAILS = "groups/details";
	private static final String GROUPS_REMOVE_MODAL = "groups/remove-group-modal";
	private static final String GROUPS_USER_MODAL = "groups/remove-user-modal";

	private final UserGroupService userGroupService;
	private final UserService userService;
	private final MessageSource messageSource;

	/**
	 * Create a new groups controller.
	 * 
	 * @param userGroupService
	 *            the {@link UserGroupService}.
	 * @param userService
	 *            the {@link UserService}.
	 * @param messageSource
	 *            the {@link MessageSource}.
	 */
	@Autowired
	public GroupsController(final UserGroupService userGroupService, final UserService userService,
			final MessageSource messageSource) {
		this.userGroupService = userGroupService;
		this.messageSource = messageSource;
		this.userService = userService;
	}

	/**
	 * Get the default index page for listing groups.
	 * 
	 * @return the route to the index page.
	 */
	@RequestMapping
	public String getIndex() {
		return GROUPS_LIST;
	}

	/**
	 * Get the page to create a new group.
	 * 
	 * @return the route to the creation page.
	 */
	@RequestMapping("/create")
	public String getCreatePage() {
		return GROUPS_CREATE;
	}

	/**
	 * Create a new {@link UserGroup}.
	 * 
	 * @param userGroup
	 *            the {@link UserGroup} from the request.
	 * @param model
	 *            the model to add violation constraints to.
	 * @param locale
	 *            the locale used by the browser.
	 * @return the route back to the creation page on validation failure, or the
	 *         destails page on success.
	 */
	@RequestMapping(path = "/create", method = RequestMethod.POST)
	public String createGroup(final @ModelAttribute UserGroup userGroup, final Model model, final Locale locale, final Principal principal) {
		logger.debug("Creating group: [ " + userGroup + "]");
		final Map<String, String> errors = new HashMap<>();

		try {
			userGroupService.create(userGroup);
			return "redirect:/groups/" + userGroup.getId();
		} catch (final ConstraintViolationException e) {
			for (final ConstraintViolation<?> v : e.getConstraintViolations()) {
				errors.put(v.getPropertyPath().toString(), v.getMessage());
			}
		} catch (final EntityExistsException | DataIntegrityViolationException e) {
			errors.put("name", messageSource.getMessage("group.name.exists", null, locale));
		}

		model.addAttribute("errors", errors);
		model.addAttribute("given_name", userGroup.getName());
		model.addAttribute("given_description", userGroup.getDescription());

		return GROUPS_CREATE;
	}

	/**
	 * Search/filter/page with datatables for {@link UserGroup}.
	 * 
	 * @param criteria
	 *            the search criteria.
	 * @param principal
	 *            the user currently logged in
	 * @return the datatables-formatted list of matching {@link UserGroup}.
	 */
	@RequestMapping("/ajax/list")
	public @ResponseBody DatatablesResponse<GroupsDataTableResponse> getGroups(
			final @DatatablesParams DatatablesCriterias criteria, final Principal principal) {
		final int currentPage = DatatablesUtils.getCurrentPage(criteria);
		final Map<String, Object> sortProperties = DatatablesUtils.getSortProperties(criteria);
		final Sort.Direction direction = (Sort.Direction) sortProperties.get("direction");
		String sortName = sortProperties.get("sort_string").toString();
		sortName = sortName.replaceAll("group.", "");
		if (sortName.equals("identifier")) {
			sortName = "id";
		}

		final String searchString = criteria.getSearch();
		final Page<UserGroup> groups = userGroupService.search(UserGroupSpecification.searchUserGroup(searchString),
				currentPage, criteria.getLength(), direction, sortName);
		final User currentUser = userService.getUserByUsername(principal.getName());
		final List<GroupsDataTableResponse> groupsWithOwnership = groups.getContent().stream()
				.map(ug -> new GroupsDataTableResponse(ug, isGroupOwner(currentUser, ug),
						currentUser.getSystemRole().equals(Role.ROLE_ADMIN)))
				.collect(Collectors.toList());
		final DataSet<GroupsDataTableResponse> groupsDataSet = new DataSet<>(groupsWithOwnership,
				groups.getTotalElements(), groups.getTotalElements());
		return DatatablesResponse.build(groupsDataSet, criteria);
	}

	/**
	 * Convenience method for checking whether or not the specified user is an
	 * owner of the group.
	 * 
	 * @param user
	 *            the {@link User} to check.
	 * @param group
	 *            the {@link UserGroup} to check.
	 * @return true if owner, false otherwise.
	 */
	private boolean isGroupOwner(final User user, final UserGroup group) {
		final Collection<UserGroupJoin> groupUsers = userGroupService.getUsersForGroup(group);
		final Optional<UserGroupJoin> currentUserGroup = groupUsers.stream().filter(j -> j.getSubject().equals(user))
				.findAny();
		if (currentUserGroup.isPresent()) {
			final UserGroupJoin j = currentUserGroup.get();
			return j.getRole().equals(UserGroupRole.GROUP_OWNER);
		} else {
			return false;
		}
	}

	/**
	 * Get the details page for a {@link UserGroup}.
	 * 
	 * @param userGroupId
	 *            the {@link UserGroup} to retrieve.
	 * @param principal
	 *            the user that's currently logged in.
	 * @param model
	 *            the model to write attributes to.
	 * @return the route to the group details page.
	 */
	@RequestMapping("/{userGroupId}")
	public String getDetailsPage(final @PathVariable Long userGroupId, final Principal principal, final Model model) {
		final UserGroup group = userGroupService.read(userGroupId);
		final Collection<UserGroupJoin> groupUsers = userGroupService.getUsersForGroup(group);
		final User currentUser = userService.getUserByUsername(principal.getName());
		final boolean isOwner = isGroupOwner(currentUser, group);

		model.addAttribute("group", group);
		model.addAttribute("isAdmin", currentUser.getSystemRole().equals(Role.ROLE_ADMIN));
		model.addAttribute("isOwner", isOwner);
		model.addAttribute("users", groupUsers);
		model.addAttribute("groupRoles", ImmutableList.of(UserGroupRole.GROUP_MEMBER, UserGroupRole.GROUP_OWNER));

		return GROUP_DETAILS;
	}

	/**
	 * Delete the specified {@link UserGroup}.
	 * 
	 * @param userGroupId
	 *            the group to delete.
	 * @param locale
	 *            the locale of the browser
	 * @return a message indicating success.
	 */
	@RequestMapping(path = "/{userGroupId}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, String> deleteGroup(final @PathVariable Long userGroupId, final Locale locale) {
		final UserGroup userGroup = userGroupService.read(userGroupId);
		userGroupService.delete(userGroupId);
		return ImmutableMap.of("result", messageSource.getMessage("group.remove.notification.success",
				new Object[] { userGroup.getName() }, locale));
	}

	/**
	 * Get the group editing page.
	 * 
	 * @param userGroupId
	 *            the group id to edit.
	 * @param model
	 *            the model to write attributes to.
	 * @return the route to the editing page.
	 */
	@RequestMapping(path = "/{userGroupId}/edit")
	public String getEditPage(final @PathVariable Long userGroupId, final Model model) {
		final UserGroup group = userGroupService.read(userGroupId);
		model.addAttribute("group", group);
		model.addAttribute("given_name", group.getName());
		model.addAttribute("given_description", group.getDescription());
		return GROUPS_EDIT;
	}

	/**
	 * Submit changes to the {@link UserGroup}.
	 * 
	 * @param userGroupId
	 *            the group ID to edit.
	 * @param name
	 *            the new name of the group.
	 * @param description
	 *            the new description of the group.
	 * @param principal
	 *            the currently logged in user.
	 * @param model
	 *            the model to add attributes to.
	 * @param locale
	 *            the locale of the browser.
	 * @return the route to the editing page on validation failure, or the
	 *         details page on success.
	 */
	@RequestMapping(path = "/{userGroupId}/edit", method = RequestMethod.POST)
	public String editGroup(final @PathVariable Long userGroupId, final @RequestParam String name,
			final @RequestParam String description, final Principal principal, final Model model, final Locale locale) {
		logger.debug("Editing group: [" + userGroupId + "]");
		final Map<String, String> errors = new HashMap<>();

		try {
			userGroupService.update(userGroupId, ImmutableMap.of("name", name, "description", description));
			return getDetailsPage(userGroupId, principal, model);
		} catch (final ConstraintViolationException e) {
			for (final ConstraintViolation<?> v : e.getConstraintViolations()) {
				errors.put(v.getPropertyPath().toString(), v.getMessage());
			}
		} catch (final EntityExistsException | DataIntegrityViolationException e) {
			errors.put("name", messageSource.getMessage("group.name.exists", null, locale));
		}

		model.addAttribute("errors", errors);
		model.addAttribute("group", userGroupService.read(userGroupId));
		model.addAttribute("given_name", name);
		model.addAttribute("given_description", description);

		return GROUPS_EDIT;
	}

	/**
	 * List the members in the group.
	 * 
	 * @param criteria
	 *            the datatables criteria to search for.
	 * @param userGroupId
	 *            the group ID to get members for.
	 * @return the datatables-formatted response with filtered users.
	 */
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

	/**
	 * Get a list of the users that are not currently members of this group.
	 * 
	 * @param userGroupId
	 *            the group ID to use as a negative filter.
	 * @param term
	 *            a filter on username to filter on.
	 * @return the collection of users that match the query.
	 */
	@RequestMapping("/{userGroupId}/ajax/availablemembers")
	public @ResponseBody Collection<User> getUsersNotInGroup(final @PathVariable Long userGroupId,
			final @RequestParam String term) {
		final UserGroup group = userGroupService.read(userGroupId);
		logger.debug("Loading users not in group [" + userGroupId + "]");
		final Collection<User> usersNotInGroup = userGroupService.getUsersNotInGroup(group);
		return usersNotInGroup.stream().filter(u -> u.getLabel().toLowerCase().contains(term))
				.collect(Collectors.toList());
	}

	/**
	 * Add a new user to the group.
	 * 
	 * @param userGroupId
	 *            the group to add to.
	 * @param userId
	 *            the new member.
	 * @param groupRole
	 *            the role this user should have.
	 * @param locale
	 *            the locale of the browser.
	 * @return a message indicating success.
	 */
	@RequestMapping(path = "/{userGroupId}/members", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> addUserToGroup(final @PathVariable Long userGroupId,
			final @RequestParam Long userId, @RequestParam String groupRole, Locale locale) {
		final User user = userService.read(userId);
		final UserGroup group = userGroupService.read(userGroupId);
		final UserGroupRole role = UserGroupRole.valueOf(groupRole);
		userGroupService.addUserToGroup(user, group, role);
		return ImmutableMap.of("result", messageSource.getMessage("group.users.add.notification.success",
				new Object[] { user.getLabel() }, locale));
	}

	/**
	 * Remove a user from a group.
	 * 
	 * @param userGroupId
	 *            the group to remove from.
	 * @param userId
	 *            the user to remove.
	 * @param locale
	 *            the locale of the browser.
	 * @return a message indicating success.
	 */
	@RequestMapping(path = "/{userGroupId}/members/{userId}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, String> removeUserFromGroup(final @PathVariable Long userGroupId,
			final @PathVariable Long userId, Locale locale) {
		final User user = userService.read(userId);
		final UserGroup group = userGroupService.read(userGroupId);
		userGroupService.removeUserFromGroup(user, group);
		return ImmutableMap.of("result", messageSource.getMessage("group.users.remove.notification.success",
				new Object[] { user.getLabel() }, locale));
	}

	/**
	 * Get a string to tell the user which group they're going to delete.
	 * 
	 * @param userGroupId
	 *            the user group that's about to be deleted.
	 * @param locale
	 *            the locale of the browser.
	 * @return a message indicating which group is going to be deleted.
	 */
	@RequestMapping(path = "/deleteConfirmModal", method = RequestMethod.POST)
	public String getDeleteGroupText(final @RequestParam Long userGroupId, final Model model) {
		final UserGroup group = userGroupService.read(userGroupId);
		model.addAttribute("group", group);
		return GROUPS_REMOVE_MODAL;
	}
	
	/**
	 * Get a string to tell the user which group they're going to delete.
	 * 
	 * @param userGroupId
	 *            the user group that's about to be deleted.
	 * @param locale
	 *            the locale of the browser.
	 * @return a message indicating which group is going to be deleted.
	 */
	@RequestMapping(path = "/removeUserModal", method = RequestMethod.POST)
	public String getRemoveUserModal(final @RequestParam Long userId, final Model model) {
		final User user = userService.read(userId);
		model.addAttribute("user", user);
		return GROUPS_USER_MODAL;
	}

	/**
	 * Convenience class for wrapping {@link UserGroup} with ownership
	 * permissions when listing.
	 */
	private static final class GroupsDataTableResponse {
		private final UserGroup group;
		private final boolean groupOwner;
		private final boolean isAdmin;

		/**
		 * Create a new row in the datatable.
		 * 
		 * @param group
		 *            the group
		 * @param groupOwner
		 *            is the current user an owner of the group
		 * @param isAdmin
		 *            is the current user an admin
		 */
		public GroupsDataTableResponse(final UserGroup group, final boolean groupOwner, final boolean isAdmin) {
			this.group = group;
			this.groupOwner = groupOwner;
			this.isAdmin = isAdmin;
		}

		@SuppressWarnings("unused")
		public UserGroup getGroup() {
			return this.group;
		}

		@SuppressWarnings("unused")
		public boolean isGroupOwner() {
			return this.groupOwner;
		}

		@SuppressWarnings("unused")
		public boolean isAdmin() {
			return this.isAdmin;
		}
	}

}

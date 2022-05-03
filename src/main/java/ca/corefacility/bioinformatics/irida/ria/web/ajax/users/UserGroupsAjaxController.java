package ca.corefacility.bioinformatics.irida.ria.web.ajax.users;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.UserGroupWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.*;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUserGroupsService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller for asynchronous request for User Groups
 */
@RestController
@RequestMapping("/ajax/user-groups")
public class UserGroupsAjaxController {
	private final UIUserGroupsService service;

	public UserGroupsAjaxController(UIUserGroupsService userGroupService) {
		this.service = userGroupService;
	}

	/**
	 * Gat a paged list of user groups
	 *
	 * @param request details about the current table page
	 * @return {@link TableResponse} for the current page of user groups
	 */
	@RequestMapping("/list")
	public ResponseEntity<TableResponse<UserGroupTableModel>> getUserGroups(@RequestBody TableRequest request) {
		return ResponseEntity.ok(service.getUserGroups(request));
	}

	/**
	 * Delete a specific user group
	 *
	 * @param id     Identifier for the user group to delete
	 * @param locale Current users locale
	 * @return Message to user about what happened
	 */
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUserGroup(@RequestParam Long id, Locale locale) {
		return ResponseEntity.ok(service.deleteUserGroup(id, locale));
	}

	/**
	 * Get the details about a user group
	 *
	 * @param groupId identifier for a user group
	 * @return details about a user group
	 */
	@RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
	public ResponseEntity<UserGroupDetails> getUserGroupDetails(@PathVariable Long groupId) {
		return ResponseEntity.ok(service.getUserGroupDetails(groupId));
	}

	/**
	 * Update the details within a user group
	 *
	 * @param groupId identifier for the user group
	 * @param update  {@link FieldUpdate} containing name of field to update and the new value
	 */
	@RequestMapping(value = "/{groupId}/update", method = RequestMethod.PUT)
	public void updateGroupDetails(@PathVariable Long groupId, @RequestBody FieldUpdate update) {
		service.updateUserGroupDetails(groupId, update);
	}

	/**
	 * Get a list of all user group roles
	 *
	 * @param locale current users {@link Locale}
	 * @return list of all internationalized user group roles
	 */
	@RequestMapping("/roles")
	public List<UserGroupRole> getUserGroupRoles(Locale locale) {
		return service.getUserGroupRoles(locale);
	}

	/**
	 * Update a group members role on the user groups
	 *
	 * @param groupId Identifier for an user group
	 * @param userId  Identifier for a user
	 * @param role    role to update the user to in the user group
	 * @param locale  current users locale
	 * @return message to user about the result of the update
	 */
	@RequestMapping(value = "/{groupId}/member/role", method = RequestMethod.PUT)
	public ResponseEntity<String> updateUserRoleOnUserGroup(@PathVariable Long groupId, @RequestParam Long userId,
			@RequestParam String role, Locale locale) {
		try {
			return ResponseEntity.ok(service.updateUserRoleOnUserGroup(groupId, userId, role, locale));
		} catch (UserGroupWithoutOwnerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}

	/**
	 * Get a listing of available users for the user group filtered by a text query
	 *
	 * @param groupId identifier for the user group
	 * @param query   string term to filter the list of users by
	 * @return listing of available users
	 */
	@RequestMapping("/{groupId}/available")
	public ResponseEntity<List<User>> getAvailableUsersForUserGroup(@PathVariable Long groupId,
			@RequestParam String query) {
		return ResponseEntity.ok(service.getAvailableUsersForUserGroup(groupId, query));
	}

	/**
	 * Add a new member to the user group
	 *
	 * @param groupId Identifier for the user group
	 * @param userId  identifier for the {@link User}
	 * @param role    role to assign to the user
	 * @param locale  current users {@link Locale}
	 * @return message to the user about the result of adding the user
	 */
	@RequestMapping(value = "/{groupId}/add", method = RequestMethod.POST)
	public ResponseEntity<String> addMemberToUserGroup(@PathVariable Long groupId,
			@RequestParam Long userId, @RequestParam String role, Locale locale) {
		return ResponseEntity.ok(service.addMemberToUserGroup(groupId, userId, role, locale));
	}

	/**
	 * Remove a member from the user group
	 *
	 * @param groupId Identifier for the user group
	 * @param userId  Identifier for the member to remove from the user group
	 * @param locale  current users {@link Locale}
	 * @return message to the user about the result of removing the member
	 */
	@RequestMapping(value = "/{groupId}/remove", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeMemberFromUserGroup(@PathVariable Long groupId, @RequestParam Long userId,
			Locale locale) {
		try {
			return ResponseEntity.ok(service.removeMemberFromUserGroup(groupId, userId, locale));
		} catch (UserGroupWithoutOwnerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}

	/**
	 * Get a full listing of all projects that this user group is on
	 *
	 * @param groupId Identifier for a user group
	 * @param locale  current users {@link Locale}
	 * @return list of {@link UserGroupProjectTableModel} that this user group is on
	 */
	@RequestMapping("/{groupId}/projects")
	public ResponseEntity<List<UserGroupProjectTableModel>> getProjectsForUserGroup(@PathVariable Long groupId,
			Locale locale) {
		return ResponseEntity.ok(service.getProjectsForUserGroup(groupId, locale));
	}

	/**
	 * Create a new {@link UserGroup}
	 * @param userGroup New {@link UserGroup} to create from UI form fields (name and role)
	 * @param locale current users {@link Locale}
	 * @return message to the user about the result of creating the group
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> createNewUserGroup(@RequestBody UserGroup userGroup, Locale locale) {
		try {
			Long id= service.createNewUserGroup(userGroup, locale);
			return ResponseEntity.ok(ImmutableMap.of("id", String.valueOf(id)));
		} catch (UIConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getErrors());
		}
	}
}

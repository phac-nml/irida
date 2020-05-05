package ca.corefacility.bioinformatics.irida.ria.web.ajax.users;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.UserGroupWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.FieldUpdate;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserGroupDetails;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserGroupRole;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUserGroupsService;

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
	public ResponseEntity<String> deleteUserGroups(@RequestParam Long id, Locale locale) {
		return ResponseEntity.ok(service.deleteUserGroup(id, locale));
	}

	@RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
	public ResponseEntity<UserGroupDetails> getUserGroupDetails(@PathVariable Long groupId) {
		return ResponseEntity.ok(service.getUserGroupDetails(groupId));
	}

	@RequestMapping(value = "/{groupId}/update", method = RequestMethod.PUT)
	public void updateGroupDetails(@PathVariable Long groupId, @RequestBody FieldUpdate update) {
		service.updateGroupDetails(groupId, update);
	}

	@RequestMapping("/roles")
	public List<UserGroupRole> getUserGroupRoles(Locale locale) {
		return service.getUserGroupRoles(locale);
	}

	@RequestMapping(value = "/{groupId}/member/role", method = RequestMethod.PUT)
	public ResponseEntity<String> updateUserRoleOnUserGroup(@PathVariable Long groupId, @RequestParam Long userId, @RequestParam String role, Locale locale) {
		try {
			return ResponseEntity.ok(service.updateUserRoleOnUserGroup(groupId, userId, role, locale));
		} catch (UserGroupWithoutOwnerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
}

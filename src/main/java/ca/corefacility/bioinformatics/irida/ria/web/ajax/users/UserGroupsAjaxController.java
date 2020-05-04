package ca.corefacility.bioinformatics.irida.ria.web.ajax.users;

import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

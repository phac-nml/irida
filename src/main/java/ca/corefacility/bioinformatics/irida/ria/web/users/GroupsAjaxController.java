package ca.corefacility.bioinformatics.irida.ria.web.users;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUserGroupService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserGroupTableModel;

@RestController
@RequestMapping("/ajax/groups")
public class GroupsAjaxController {
	private final UIUserGroupService userGroupService;

	@Autowired
	public GroupsAjaxController(UIUserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}

	/**
	 * Get a paged list of user groups
	 *
	 * @param request {@link TableRequest} defining the filters and sort of the list
	 * @return {@link TableResponse}
	 */
	@RequestMapping("")
	public ResponseEntity<TableResponse<UserGroupTableModel>> getPagedUserGroups(@RequestBody TableRequest request) {
		return ResponseEntity.ok(userGroupService.getPagedUserGroups(request));
	}

	/**
	 * Delete a user group
	 *
	 * @param id     identifier for a user group to delete
	 * @param locale current users {@link Locale}
	 * @return message to user about the result of the deletion
	 */
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUserGroup(@RequestParam Long id, Locale locale) {
		return ResponseEntity.ok(userGroupService.deleteUserGroup(id, locale));
	}
}

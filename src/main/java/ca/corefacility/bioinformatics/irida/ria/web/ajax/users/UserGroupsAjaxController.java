package ca.corefacility.bioinformatics.irida.ria.web.ajax.users;

import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUserGroupsService;

@RestController
@RequestMapping("/ajax/user-groups")
public class UserGroupsAjaxController {
	private final UIUserGroupsService service;

	public UserGroupsAjaxController(UIUserGroupsService userGroupService) {
		this.service = userGroupService;
	}

	@RequestMapping("/list")
	public ResponseEntity<TableResponse<UserGroupTableModel>> getUserGroups(@RequestBody TableRequest request) {
		return ResponseEntity.ok(service.getUserGroups(request));
	}

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUserGroups(@RequestParam Long id, Locale locale) {
		return ResponseEntity.ok(service.deleteUserGroup(id, locale));
	}
}

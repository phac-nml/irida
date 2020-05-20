package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMemberRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectUserGroupsTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectUserGroupsService;

@RestController
@RequestMapping("/ajax/projects/{projectId}/user-groups")
public class ProjectUserGroupsAjaxController {
	private final UIProjectUserGroupsService service;

	@Autowired
	public ProjectUserGroupsAjaxController(UIProjectUserGroupsService service) {
		this.service = service;
	}

	@RequestMapping("")
	public ResponseEntity<TableResponse<ProjectUserGroupsTableModel>> getProjectUserGroups(@PathVariable Long projectId,
			@RequestBody TableRequest request) {
		return ResponseEntity.ok(service.getUserGroupsForProject(projectId, request));
	}

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeUserGroupFromProject(@PathVariable long projectId, @RequestParam long groupId,
			Locale locale) {
		return ResponseEntity.ok(service.removeUserGroupFromProject(projectId, groupId, locale));
	}

	@RequestMapping("/available")
	public ResponseEntity<List<UserGroup>> getAvailableUserGroupsForProject(@PathVariable Long projectId,
			@RequestParam String query) {
		return ResponseEntity.ok(service.getAvailableUserGroupsForProject(projectId, query));
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<String> addUserGroupToProject(@PathVariable Long projectId,
			@RequestBody NewMemberRequest request, Locale locale) {
		return ResponseEntity.ok(service.addUserGroupToProject(projectId, request, locale));
	}

	@RequestMapping(value = "/role", method = RequestMethod.PUT)
	public ResponseEntity<String> updateUserGroupRoleOnProject(@PathVariable Long projectId, @RequestParam Long id,
			@RequestParam String role, Locale locale) {
		try {
			return ResponseEntity.ok(service.updateUserGroupRoleOnProject(projectId, id, role, locale));
		} catch (ProjectWithoutOwnerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
}

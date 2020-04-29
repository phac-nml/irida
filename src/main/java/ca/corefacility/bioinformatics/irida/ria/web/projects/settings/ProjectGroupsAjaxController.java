package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.NewProjectGroupRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectGroupsService;

/**
 * Controller for AJAX request pertaining to Project UserGroups.
 */
@RestController
@RequestMapping("/ajax/projects/{projectId}/groups")
public class ProjectGroupsAjaxController {
	private final UIProjectGroupsService groupsService;

	@Autowired
	public ProjectGroupsAjaxController(UIProjectGroupsService groupsService) {
		this.groupsService = groupsService;
	}

	/**
	 * Get a paged listing of project groups based on parameters set in the table request.
	 *
	 * @param projectId    identifier for the current project
	 * @param tableRequest details about the current page of the table
	 * @return sorted and filtered list of project user groups
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<TableResponse<ProjectGroupTableModel>> getProjectGroups(@PathVariable Long projectId,
			@RequestBody TableRequest tableRequest) {
		return ResponseEntity.ok(groupsService.getProjectGroups(projectId, tableRequest));
	}

	/**
	 * Search for user groups that are not currently on the project
	 *
	 * @param projectId identifier for the current project
	 * @param query     to search user group names by
	 * @return a filtered list of user groups
	 */
	@RequestMapping("/search")
	public ResponseEntity<List<ProjectGroupTableModel>> searchAvailableUserGroups(@PathVariable Long projectId,
			@RequestParam String query) {
		return ResponseEntity.ok(groupsService.searchAvailableGroups(projectId, query));
	}

	/**
	 * Add a new user group to the current project
	 *
	 * @param projectId identifier for the current project
	 * @param request   information about the user group (identifier and role)
	 * @param locale    currently logged in users locale
	 * @return an internationalized message stating the result of adding the new group
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<String> addUserGroupToProject(@PathVariable Long projectId,
			@RequestBody NewProjectGroupRequest request, Locale locale) {
		return ResponseEntity.ok(groupsService.addUserGroupToProject(projectId, request, locale));
	}

	/**
	 * Updated an user groups role on the current project
	 *
	 * @param projectId identifier for the current project
	 * @param id        identifier for the user group
	 * @param role      string representation of the new role
	 * @param locale    currently logged in users locale
	 * @return an internationalized message stating the result of the change in role.
	 */
	@RequestMapping(value = "/role", method = RequestMethod.PUT)
	public ResponseEntity<String> updateGroupRoleOnProject(@PathVariable Long projectId, @RequestParam Long id,
			@RequestParam String role, Locale locale) {
		try {
			return ResponseEntity.ok(groupsService.updateGroupRoleOnProject(projectId, id, role, locale));
		} catch (UIProjectWithoutOwnerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}

	/**
	 * Remove a user group from the current project
	 *
	 * @param projectId identifier for the current project
	 * @param id        identifier for the group to remove
	 * @param locale    currently logged in users locale
	 * @return an internationalized message stating the result of removing the user group
	 */
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeGroupFromProject(@PathVariable Long projectId, @RequestParam Long id,
			Locale locale) {
		try {
			return ResponseEntity.ok(groupsService.removeUserGroupFromProject(projectId, id, locale));
		} catch (UIProjectWithoutOwnerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects;

import java.util.List;
import java.util.Locale;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMemberRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectUserGroupsTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectUserGroupsService;

/**
 * UI Ajax Controller for handling project user groups
 */
@RestController
@RequestMapping("/ajax/projects/groups")
public class ProjectUserGroupsAjaxController {
	private final UIProjectUserGroupsService service;

	@Autowired
	public ProjectUserGroupsAjaxController(UIProjectUserGroupsService service) {
		this.service = service;
	}

	/**
	 * Get a table page of {@link ProjectUserGroupsTableModel}
	 *
	 * @param projectId Identifier for a project
	 * @param request   {@link TableRequest} details about the current page of the table
	 * @return {@link TableResponse}
	 */
	@RequestMapping("")
	public ResponseEntity<TableResponse<ProjectUserGroupsTableModel>> getProjectUserGroups(@RequestParam Long projectId,
			@RequestBody TableRequest request) {
		return ResponseEntity.ok(service.getUserGroupsForProject(projectId, request));
	}

	/**
	 * Remove a user group from a project
	 *
	 * @param projectId Identifier for a project
	 * @param groupId   Identifier for an user group
	 * @param locale    current users locale
	 * @return message to user about the result of removing the user group
	 */
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<String> removeUserGroupFromProject(@RequestParam long projectId, @RequestParam long groupId,
			Locale locale) {
		return ResponseEntity.ok(service.removeUserGroupFromProject(projectId, groupId, locale));
	}

	/**
	 * Get a list of user groups that are not on the current project
	 *
	 * @param projectId Identifier for the current project
	 * @param query     Filter string to search the existing user groups by
	 * @return List of user groups
	 */
	@RequestMapping("/available")
	public ResponseEntity<List<UserGroup>> getAvailableUserGroupsForProject(@RequestParam Long projectId,
			@RequestParam String query) {
		return ResponseEntity.ok(service.getAvailableUserGroupsForProject(projectId, query));
	}

	/**
	 * Add a user group to the current project
	 *
	 * @param projectId Identifier for a project
	 * @param request   Identifier for an user group
	 * @param locale    Current users locale
	 * @return message to user about the outcome of adding the user group to the project
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<String> addUserGroupToProject(@RequestParam Long projectId,
			@RequestBody NewMemberRequest request, Locale locale) {
		try {
			return ResponseEntity.ok(service.addUserGroupToProject(projectId, request, locale));
		} catch (EntityExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	/**
	 * Update the project role of a user group on the current project
	 *
	 * @param projectId   Identifier for a project
	 * @param id          Identifier for an user group
	 * @param projectRole Project role to update the user group to
	 * @param locale      Current users locale
	 * @return message to user about the result of the update
	 */
	@RequestMapping(value = "/role", method = RequestMethod.PUT)
	public ResponseEntity<String> updateUserGroupRoleOnProject(@RequestParam Long projectId, @RequestParam Long id,
			String projectRole, Locale locale) {
		try {
			return ResponseEntity.ok(service.updateUserGroupRoleOnProject(projectId, id, projectRole, locale));
		} catch (UIProjectWithoutOwnerException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		} catch (UIConstraintViolationException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getErrorMessage());
		}
	}

	/**
	 * Update the project metadata role of a user group on the current project
	 *
	 * @param projectId    Identifier for a project
	 * @param id           Identifier for an user group
	 * @param metadataRole metadata role to update for the user group
	 * @param locale       Current users locale
	 * @return message to user about the result of the update
	 */
	@RequestMapping(value = "/metadata-role", method = RequestMethod.PUT)
	public ResponseEntity<String> updateUserGroupMetadataRoleOnProject(@RequestParam Long projectId,
			@RequestParam Long id, String metadataRole, Locale locale) {
		try {
			return ResponseEntity.ok(service.updateUserGroupMetadataRoleOnProject(projectId, id, metadataRole, locale));
		} catch (UIConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getErrorMessage());
		}
	}
}

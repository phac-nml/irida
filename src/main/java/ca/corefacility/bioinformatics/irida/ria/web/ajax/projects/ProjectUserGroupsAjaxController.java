package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<String> removeUserGroupFromProject(@PathVariable long projectId, @RequestParam long groupId, Locale locale) {
		return ResponseEntity.ok(service.removeUserGroupFromProject(projectId, groupId, locale));
	}
}

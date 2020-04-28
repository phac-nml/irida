package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.NewProjectGroupRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectGroupsService;

@RestController
@RequestMapping("/ajax/projects/{projectId}/groups")
public class ProjectGroupsAjaxController {
	private UIProjectGroupsService groupsService;

	@Autowired
	public ProjectGroupsAjaxController(UIProjectGroupsService groupsService) {
		this.groupsService = groupsService;
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<TableResponse<ProjectGroupTableModel>> getProjectGroups(@PathVariable Long projectId,
			@RequestBody TableRequest tableRequest) {
		return ResponseEntity.ok(groupsService.getProjectGroups(projectId, tableRequest));
	}

	@RequestMapping("/search")
	public ResponseEntity<List<ProjectGroupTableModel>> searchAvailableUserGroups(@PathVariable Long projectId,
			@RequestParam String query) {
		return ResponseEntity.ok(groupsService.searchAvailableGroups(projectId, query));
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<String> addUserGroupToProject(@PathVariable Long projectId, @RequestBody
			NewProjectGroupRequest request, Locale locale) {
		return ResponseEntity.ok(groupsService.addUserGroupToProject(projectId, request, locale));
	}
}

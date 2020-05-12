package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectUserGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectUserGroupService;

@RestController
@RequestMapping("/ajax/projects/{projectId}/groups")
public class ProjectUserGroupsAjaxController {
	private final UIProjectUserGroupService service;

	@Autowired
	public ProjectUserGroupsAjaxController(UIProjectUserGroupService service) {
		this.service = service;
	}

	@RequestMapping("")
	public ResponseEntity<TableResponse<ProjectUserGroupTableModel>> getProjectUserGroups(@PathVariable Long projectId,
			@RequestBody TableRequest request) {
		return ResponseEntity.ok(service.getProjectUserGroups(projectId, request));
	}
}

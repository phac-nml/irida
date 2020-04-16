package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.ProjectDetailsResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

@RestController
@RequestMapping("/ajax/projects/{projectId}/details")
public class ProjectDetailsAjaxController {
	private final ProjectService projectService;

	@Autowired
	public ProjectDetailsAjaxController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@RequestMapping("")
	public ResponseEntity<ProjectDetailsResponse> getProjectDetails(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		return ResponseEntity.ok(new ProjectDetailsResponse(project));
	}
}

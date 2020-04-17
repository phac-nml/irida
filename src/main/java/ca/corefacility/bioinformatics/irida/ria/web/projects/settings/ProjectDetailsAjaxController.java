package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.ProjectDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.UpdateProjectAttributeRequest;
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

	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public ResponseEntity<String> updateProjectDetails(@PathVariable Long projectId,
			@RequestBody UpdateProjectAttributeRequest request) {
		Project project = projectService.read(projectId);
		switch (request.getField()) {
		case "label":
			project.setName(request.getValue());
			break;
		case "description":
			project.setProjectDescription(request.getValue());
			break;
		default:
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("ERROR");
		}
		projectService.update(project);
		return ResponseEntity.ok("SUCCESS");
	}
}

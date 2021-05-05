package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAssociatedProjectsService;

@Controller
@RequestMapping("/ajax/projects/associated")
public class ProjectAjaxAssociatedProjectsController {
	private final UIAssociatedProjectsService service;

	public ProjectAjaxAssociatedProjectsController(UIAssociatedProjectsService service) {
		this.service = service;
	}

	/**
	 * Get a list of all projects associated with the current project.  If the user is a manager or administrator, the
	 * list will also contain all projects they have access to.
	 *
	 * @param projectId project identifier for the currently active project
	 * @return list of projects
	 */
	@GetMapping("")
	public ResponseEntity<List<AssociatedProject>> getAssociatedProjects(@RequestParam long projectId) {
		return ResponseEntity.ok(service.getAssociatedProjects(projectId));
	}

	/**
	 * Create a new associated project linkage
	 *
	 * @param projectId           identifier for the current project
	 * @param associatedProjectId identifier for the project to associate
	 */
	@PostMapping("")
	public void addAssociatedProject(@RequestParam long projectId, @RequestParam long associatedProjectId) {
		service.addAssociatedProject(projectId, associatedProjectId);
	}

	/**
	 * Remove an associated project linkage
	 *
	 * @param projectId           identifier for the current project
	 * @param associatedProjectId identifier for the project to associate
	 */
	@DeleteMapping("")
	public void removeAssociatedProject(@RequestParam long projectId, @RequestParam long associatedProjectId) {
		service.removeAssociatedProject(projectId, associatedProjectId);
	}
}

package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.util.List;
import java.util.Locale;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAssociatedProjectsService;

/**
 * Ajax Controller for handling associated projects
 */
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
	 * @param locale              current users locale
	 */
	@PostMapping("")
	public ResponseEntity<AjaxResponse> addAssociatedProject(@RequestParam long projectId,
			@RequestParam long associatedProjectId, Locale locale) {
		try {
			service.addAssociatedProject(projectId, associatedProjectId, locale);
			return ResponseEntity.ok(new AjaxSuccessResponse(""));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Remove an associated project linkage
	 *
	 * @param projectId           identifier for the current project
	 * @param associatedProjectId identifier for the project to associate
	 * @param locale              current users locale
	 */
	@DeleteMapping("")
	public ResponseEntity<AjaxResponse> removeAssociatedProject(@RequestParam long projectId,
			@RequestParam long associatedProjectId, Locale locale) {
		try {
			service.removeAssociatedProject(projectId, associatedProjectId, locale);
			return ResponseEntity.ok(new AjaxSuccessResponse(""));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}
}

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
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIAddAssociatedProjectException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIRemoveAssociatedProjectException;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAssociatedProjectsService;

/**
 * Ajax Controller for handling associated projects
 */
@Controller
@RequestMapping("/ajax/projects/associated")
public class ProjectAssociatedProjectsAjaxController {
	private final UIAssociatedProjectsService service;

	public ProjectAssociatedProjectsAjaxController(UIAssociatedProjectsService service) {
		this.service = service;
	}

	/**
	 * Get a list of all projects associated with the current project. If the user is a manager or administrator, the
	 * list will also contain all projects they have access to.
	 *
	 * @param projectId project identifier for the currently active project
	 * @return list of projects
	 */
	@GetMapping("")
	public ResponseEntity<List<AssociatedProject>> getAssociatedProjects(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getAssociatedProjects(projectId));
	}

	/**
	 * Get a list of all projects associated with the current project.
	 *
	 * @param projectId project identifier for the currently active project
	 * @return list of projects
	 */
	@GetMapping("/list")
	public ResponseEntity<List<AssociatedProject>> getAssociatedProjectsForProject(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getAssociatedProjectsForProject(projectId));
	}

	/**
	 * Create a new associated project linkage
	 *
	 * @param projectId           identifier for the current project
	 * @param associatedProjectId identifier for the project to associate
	 * @param locale              current users locale
	 * @return The result of adding the associated project
	 */
	@PostMapping("")
	public ResponseEntity<AjaxResponse> addAssociatedProject(@RequestParam Long projectId,
			@RequestParam Long associatedProjectId, Locale locale) {
		try {
			service.addAssociatedProject(projectId, associatedProjectId, locale);
			return ResponseEntity.ok(new AjaxSuccessResponse(""));
		} catch (UIAddAssociatedProjectException e) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Remove an associated project linkage
	 *
	 * @param projectId           identifier for the current project
	 * @param associatedProjectId identifier for the project to associate
	 * @param locale              current users locale
	 * @return the result of removing the project
	 */
	@DeleteMapping("")
	public ResponseEntity<AjaxResponse> removeAssociatedProject(@RequestParam Long projectId,
			@RequestParam Long associatedProjectId, Locale locale) {
		try {
			service.removeAssociatedProject(projectId, associatedProjectId, locale);
			return ResponseEntity.ok(new AjaxSuccessResponse(""));
		} catch (UIRemoveAssociatedProjectException e) {
			return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(new AjaxErrorResponse(e.getMessage()));
		}
	}
}

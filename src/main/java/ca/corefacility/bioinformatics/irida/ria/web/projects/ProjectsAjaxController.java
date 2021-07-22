package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.CopySamplesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.Role;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectsService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;

/**
 * Controller for handling all ajax requests for Projects.
 */
@RestController
@RequestMapping("/ajax/projects")
public class ProjectsAjaxController {
	private final UIProjectsService projectsService;
	private final UISampleService sampleService;


	@Autowired
	public ProjectsAjaxController(UIProjectsService projectsService, UISampleService sampleService) {
		this.projectsService = projectsService;
		this.sampleService = sampleService;
	}

	/**
	 * Handle request for get a filtered and sorted list of projects for a user or administrator
	 *
	 * @param tableRequest {@link TableRequest} Details about what is needed in the table (sort, filter, and search).
	 * @param admin           {@link Boolean} Is the user on an administration page.
	 * @return {@link TableResponse}
	 */
	@RequestMapping
	public ResponseEntity<TableResponse> getPagedProjectsForUser(@RequestBody TableRequest tableRequest,
			@RequestParam Boolean admin) {
		return ResponseEntity.ok(projectsService.getPagedProjects(tableRequest, admin));
	}

	/**
	 * Get a list of all roles available on a project
	 *
	 * @param locale - {@link Locale} of the current user
	 * @return list of roles and their internationalized strings
	 */
	@RequestMapping("/roles")
	public ResponseEntity<List<Role>> getProjectRoles(Locale locale) {
		return ResponseEntity.ok(projectsService.getProjectRoles(locale));
	}

	@GetMapping("/share-samples/projects")
	public List<Project> getProjectToShare(@RequestParam long current) {
		return projectsService.getProjectToShare(current);
	}

	@PostMapping("/share-samples/sampleIds")
	public List<Long> getProjectToShare(@RequestParam long projectId, @RequestBody List<Long> sampleIds) {
		return sampleService.getCommonSampleIdentifiers(projectId, sampleIds);
	}

	@PutMapping("/share-samples/copy")
	public ResponseEntity<AjaxResponse> copySamplesToProject(@RequestBody CopySamplesRequest request) {
		sampleService.copySamplesToProject(request);
		return ResponseEntity.ok(new AjaxSuccessResponse("YAY"));
	}
}

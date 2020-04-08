package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.Role;
import ca.corefacility.bioinformatics.irida.ria.web.services.ProjectsService;

/**
 * Controller for handling all ajax requests on the Projects listing page.
 */
@RestController
@RequestMapping("/ajax/projects")
public class ProjectsAjaxController {
	private final ProjectsService projectsService;


	@Autowired
	public ProjectsAjaxController(ProjectsService projectsService) {
		this.projectsService = projectsService;
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

	@RequestMapping("/roles")
	public ResponseEntity<List<Role>> getProjectRoles(Locale locale) {
		return ResponseEntity.ok(projectsService.getProjectRoles(locale));
	}
}

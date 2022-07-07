package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.CreateProjectRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.Role;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectsService;

/**
 * Controller for handling all ajax requests for Projects.
 */
@RestController
@RequestMapping("/ajax/projects")
public class ProjectsAjaxController {
	private final UIProjectsService projectsService;
	private final MessageSource messageSource;

	@Autowired
	public ProjectsAjaxController(UIProjectsService UIProjectsService, MessageSource messageSource) {
		this.projectsService = UIProjectsService;
		this.messageSource = messageSource;
	}

	/**
	 * Create a new project.
	 *
	 * @param request {@link CreateProjectRequest} containing the name for the project and any other details
	 * @param locale  {@link Locale} for the currently logged-in user
	 * @return Response will contain the created project identifier, or an error message
	 */
	@PostMapping("/new")
	public AjaxResponse createNewProject(@RequestBody CreateProjectRequest request, Locale locale) {
		try {
			return new AjaxCreateItemSuccessResponse(projectsService.createProject(request));
		} catch (Exception e) {
			return new AjaxErrorResponse(
					messageSource.getMessage("server.CreateProject.error", new Object[] {}, locale));
		}
	}

	/**
	 * Handle request for getting a list of projects for a user
	 *
	 * @return {@link List} of {@link Project}s
	 */
	@RequestMapping("/user")
	public ResponseEntity<List<Project>> getProjectsForUser() {
		return ResponseEntity.ok(projectsService.getProjectsForUser());
	}

	/**
	 * Handle request for getting a filtered and sorted list of projects for a user or administrator
	 *
	 * @param tableRequest {@link TableRequest} Details about what is needed in the table (sort, filter, and search).
	 * @param admin        {@link Boolean} Is the user on an administration page.
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

	/**
	 * Get a list of projects that the user can share sample to.
	 *
	 * @param currentId Current project identifier
	 * @return {@link List} of {@link Project}s
	 */
	@RequestMapping("/samples-share/projects")
	public List<Project> getPotentialProjectsToShareTo(@RequestParam long currentId) {
		return projectsService.getPotentialProjectsToShareTo(currentId);
	}
}

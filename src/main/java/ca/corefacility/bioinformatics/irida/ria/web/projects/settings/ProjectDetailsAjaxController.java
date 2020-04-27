package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.util.Locale;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.ProjectDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.UpdateProjectAttributeRequest;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * Handle asynchronous requests for the UI project details page.
 */
@RestController
@RequestMapping("/ajax/projects/{projectId}/details")
public class ProjectDetailsAjaxController {
	private final ProjectService projectService;
	private final MessageSource messageSource;

	@Autowired
	public ProjectDetailsAjaxController(ProjectService projectService, MessageSource messageSource) {
		this.projectService = projectService;
		this.messageSource = messageSource;
	}

	/**
	 * Get general details about the project.
	 *
	 * @param projectId {@link Long} identifier for the project
	 * @return {@link ResponseEntity} containing the project details
	 */
	@RequestMapping("")
	public ResponseEntity<ProjectDetailsResponse> getProjectDetails(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);
		return ResponseEntity.ok(new ProjectDetailsResponse(project));
	}

	/**
	 * Update a field within the project details.
	 *
	 * @param projectId {@link Long} identifier for the project
	 * @param request   {@link UpdateProjectAttributeRequest} details about which field to update
	 * @param locale    {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the update.
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public ResponseEntity<String> updateProjectDetails(@PathVariable Long projectId,
			@RequestBody UpdateProjectAttributeRequest request, Locale locale) {
		try {
			Project project = projectService.read(projectId);
			switch (request.getField()) {
			case "label":
				project.setName(request.getValue());
				break;
			case "description":
				project.setProjectDescription(request.getValue());
				break;
			case "organism":
				project.setOrganism(request.getValue());
				break;
			default:
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(messageSource.getMessage("server.ProjectDetails.error",
								new Object[] { request.getField() }, locale));
			}
			projectService.update(project);
			return ResponseEntity.ok(messageSource.getMessage("server.ProjectDetails.success",
					new Object[] { request.getField(), request.getValue() }, locale));
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest()
					.body(messageSource.getMessage("server.ProjectDetails.error-constraint", new Object[] {}, locale));
		}
	}
}

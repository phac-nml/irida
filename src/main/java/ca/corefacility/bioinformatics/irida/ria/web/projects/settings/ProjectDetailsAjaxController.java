package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.util.Locale;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.Coverage;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.exceptions.UpdateException;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.UpdateProjectAttributeRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectsService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.google.common.base.Strings;

/**
 * Handle asynchronous requests for the UI project details page.
 */
@RestController
@RequestMapping("/ajax/project/details")
public class ProjectDetailsAjaxController {
	private final ProjectService projectService;
	private final UIProjectsService service;
	private final UIMetadataService metadataService;
	private final MessageSource messageSource;

	@Autowired
	public ProjectDetailsAjaxController(ProjectService projectService, UIProjectsService service,
			UIMetadataService metadataService, MessageSource messageSource) {
		this.projectService = projectService;
		this.service = service;
		this.metadataService = metadataService;
		this.messageSource = messageSource;
	}

	/**
	 * Get general details about the project.
	 *
	 * @param projectId {@link Long} identifier for the project
	 * @return {@link ResponseEntity} containing the project details
	 */
	@RequestMapping("")
	public ResponseEntity<ProjectDetailsResponse> getProjectDetails(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getProjectInfo(projectId));
	}

	/**
	 * Update a field within the project details.
	 *
	 * @param projectId {@link Long} identifier for the project
	 * @param request   {@link UpdateProjectAttributeRequest} details about which field to update
	 * @param locale    {@link Locale} for the currently logged in user
	 * @return {@link ResponseEntity} explaining to the user the results of the update.
	 */
	@PutMapping("")
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public ResponseEntity<AjaxResponse> updateProjectDetails(@RequestParam Long projectId,
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
						.body(new AjaxErrorResponse(messageSource.getMessage("server.ProjectDetails.error",
								new Object[] { request.getField() }, locale)));
			}
			projectService.update(project);
			String message;
			if (Strings.isNullOrEmpty(request.getValue())) {
				message = messageSource.getMessage("server.ProjectDetails.success-empty",
						new Object[] { request.getField() }, locale);

			} else {
				message = messageSource.getMessage("server.ProjectDetails.success",
						new Object[] { request.getField(), request.getValue() }, locale);
			}
			return ResponseEntity.ok(new AjaxSuccessResponse(message));
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest()
					.body(new AjaxErrorResponse(messageSource.getMessage("server.ProjectDetails.error-constraint", new Object[] {}, locale)));
		}
	}

	/**
	 * Set a default metadata template for a project
	 *
	 * @param templateId Identifier for the metadata template to set as default.
	 * @param projectId  Identifier for the project to set the metadata template as default for.
	 * @param locale     Current users {@link Locale}
	 * @return {@link AjaxSuccessResponse} with the success message
	 */
	@PutMapping("/default-template")
	public ResponseEntity<AjaxResponse> setDefaultMetadataTemplate(@RequestParam Long templateId,
			@RequestParam Long projectId, Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(metadataService.setDefaultMetadataTemplate(templateId, projectId, locale)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Update the priority for analyses for a project.
	 *
	 * @param projectId identifier for a {@link Project}
	 * @param priority  the new priority for analyses
	 * @param locale    current users locale
	 * @return message to user about the update ot the priority
	 */
	@PutMapping("/priority")
	public ResponseEntity<AjaxResponse> updateProcessingPriority(@RequestParam long projectId,
			@RequestParam AnalysisSubmission.Priority priority, Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(service.updateProcessingPriority(projectId, priority, locale)));
		} catch (UpdateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Update the minimum/maximum coverage or genome size for the project
	 *
	 * @param projectId identifier for the project
	 * @param coverage  minimum/maximum coverage or genome size for the project
	 * @param locale    current users locale
	 * @return Message to user about the update
	 */
	@PutMapping("/coverage")
	public ResponseEntity<AjaxResponse> updateProcessingCoverage(@RequestParam long projectId,
			@RequestBody Coverage coverage, Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(service.updateProcessingCoverage(coverage, projectId, locale)));
		} catch (UpdateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Delete a project
	 *
	 * @param projectId identifier for a project
	 * @return an indication to the user about the result of the update
	 */
	@DeleteMapping("/delete")
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public ResponseEntity<AjaxResponse> deleteProject(@RequestParam long projectId, Locale locale) {
		try {
			service.deleteProject(projectId);
			return ResponseEntity.ok(new AjaxSuccessResponse(""));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new AjaxErrorResponse(
							messageSource.getMessage("server.DeleteProject.error", new Object[] {}, locale)));
		}
	}
}

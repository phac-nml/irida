package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.AnalysisTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.Coverage;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.exceptions.UpdateException;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSettingsService;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller to handle all asynchronous call from the project settings UI.
 */
@RestController
@RequestMapping("/ajax/projects/{projectId}/settings")
public class ProjectSettingsAjaxController {
	private final ProjectService projectService;
	private final UserService userService;
	private final ProjectOwnerPermission projectOwnerPermission;
	private final UIPipelineService pipelineService;
	private final UIProjectSettingsService settingsService;

	@Autowired
	public ProjectSettingsAjaxController(ProjectService projectService, ProjectOwnerPermission projectOwnerPermission,
			UserService userService, UIPipelineService pipelineService, UIProjectSettingsService settingsService) {
		this.projectService = projectService;
		this.projectOwnerPermission = projectOwnerPermission;
		this.userService = userService;
		this.pipelineService = pipelineService;
		this.settingsService = settingsService;
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
	public ResponseEntity<AjaxResponse> updateProcessingPriority(@PathVariable long projectId,
			@RequestParam AnalysisSubmission.Priority priority, Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(settingsService.updateProcessingPriority(projectId, priority, locale)));
		} catch (UpdateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Get the minimum/maximum coverage and genome size for the project
	 *
	 * @param projectId identifier for the project
	 * @return {@link Coverage}
	 */
	@GetMapping("/coverage")
	public Coverage getProcessingCoverage(@PathVariable Long projectId) {
		return settingsService.getProcessingCoverageForProject(projectId);
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
	public ResponseEntity<AjaxResponse> updateProcessingCoverage(@PathVariable long projectId,
			@RequestBody Coverage coverage, Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(settingsService.updateProcessingCoverage(coverage, projectId, locale)));
		} catch (UpdateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Get all the automated workflow (analysis templates) for the current project
	 *
	 * @param projectId project identifier
	 * @param locale    current users locale
	 * @return list of automated templates on the current project
	 */
	@GetMapping("/analysis-templates")
	public List<AnalysisTemplate> getProjectAnalysisTemplates(@PathVariable long projectId, Locale locale) {
		return pipelineService.getProjectAnalysisTemplates(projectId, locale);
	}

	/**
	 * Remove an automated workflow (analysis templates) for the current project
	 *
	 * @param templateId identifier for an automated workflow
	 * @param projectId  identifier for a project
	 * @param locale     current users locale
	 * @return message to user about the outcome of the removal
	 */
	@DeleteMapping("/analysis-templates")
	public ResponseEntity<AjaxResponse> removeProjectAnalysisTemplates(@RequestParam long templateId,
			@PathVariable long projectId, Locale locale) {
		return ResponseEntity.ok(
				new AjaxSuccessResponse(pipelineService.removeProjectAutomatedPipeline(templateId, projectId, locale)));
	}
}

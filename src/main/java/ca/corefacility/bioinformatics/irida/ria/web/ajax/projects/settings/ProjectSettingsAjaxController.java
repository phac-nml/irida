package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.AnalysisTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineService;

/**
 * Controller to handle all asynchronous call from the project settings UI.
 */
@RestController
@RequestMapping("/ajax/projects/{projectId}/settings")
public class ProjectSettingsAjaxController {
	private final UIPipelineService pipelineService;

	@Autowired
	public ProjectSettingsAjaxController(UIPipelineService pipelineService) {
		this.pipelineService = pipelineService;
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

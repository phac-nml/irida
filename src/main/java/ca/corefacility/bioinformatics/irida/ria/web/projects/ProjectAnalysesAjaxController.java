package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectAnalysesService;

/**
 * Controller for handling all ajax requests for Project Single Sample Analyses Outputs.
 */
@RestController
@RequestMapping("/ajax/projects/analyses-outputs")
public class ProjectAnalysesAjaxController {

	private UIProjectAnalysesService uiProjectAnalysesService;

	@Autowired
	public ProjectAnalysesAjaxController(UIProjectAnalysesService uiProjectAnalysesService) {
		this.uiProjectAnalysesService = uiProjectAnalysesService;
	}

	/**
	 * Get all the shared single sample analysis outputs for the project
	 *
	 * @param projectId {@link ca.corefacility.bioinformatics.irida.model.project.Project} id
	 * @return a response containing a list of filtered {@link ProjectSampleAnalysisOutputInfo} single sample analysis outputs
	 */
	@GetMapping("/shared")
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getSharedSingleSampleOutputs(
			@RequestParam Long projectId) {
		return ResponseEntity.ok(uiProjectAnalysesService.getSharedSingleSampleOutputs(projectId));
	}

	@GetMapping("/automated")
	public String getAutomatedSingleSampleOutputs(@RequestParam Long projectId) {
		return "YAYYYY THIS WORKS AUTOMATED";
	}

	@GetMapping("/download-shared")
	public void downloadSharedSingleSampleOutputs(@RequestParam Long projectId) {
	}

	@GetMapping("/download-automated")
	public void downloadAutomatedSingleSampleOutputs(@RequestParam Long projectId) {
	}
}

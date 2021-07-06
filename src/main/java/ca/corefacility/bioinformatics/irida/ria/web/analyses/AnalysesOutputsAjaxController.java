package ca.corefacility.bioinformatics.irida.ria.web.analyses;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnalysesOutputsService;

/**
 * Controller for handling all ajax requests for Single Sample Analyses Outputs.
 */
@RestController
@RequestMapping("/ajax/analyses-outputs")
public class AnalysesOutputsAjaxController {

	private UIAnalysesOutputsService uiAnalysesOutputsService;

	@Autowired
	public AnalysesOutputsAjaxController(UIAnalysesOutputsService uiProjectAnalysesService) {
		this.uiAnalysesOutputsService = uiProjectAnalysesService;
	}

	/**
	 * Get all the shared single sample analysis outputs for the project
	 *
	 * @param projectId {@link ca.corefacility.bioinformatics.irida.model.project.Project} id
	 * @return a response containing a list of filtered {@link ProjectSampleAnalysisOutputInfo} shared single sample analysis outputs
	 */
	@GetMapping("/shared")
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getSharedSingleSampleOutputs(
			@RequestParam Long projectId) {
		return ResponseEntity.ok(uiAnalysesOutputsService.getSharedSingleSampleOutputs(projectId));
	}

	/**
	 * Get all the automated single sample analysis outputs for the project
	 *
	 * @param projectId {@link ca.corefacility.bioinformatics.irida.model.project.Project} id
	 * @return a response containing a list of filtered {@link ProjectSampleAnalysisOutputInfo} automated single sample analysis outputs
	 */
	@GetMapping("/automated")
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getAutomatedSingleSampleOutputs(@RequestParam Long projectId) {
		return ResponseEntity.ok(uiAnalysesOutputsService.getAutomatedSingleSampleOutputs(projectId));
	}

	/**
	 * Get all the user single sample analysis outputs
	 *
	 * @param principal Currently logged in user.
	 * @return a response containing a list of filtered {@link ProjectSampleAnalysisOutputInfo} user single sample analysis outputs
	 */
	@RequestMapping(value = "/user")
	@ResponseBody
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getAllUserAnalysisOutputInfo(Principal principal) {
		return ResponseEntity.ok(uiAnalysesOutputsService.getUserSingleSampleOutputs(principal));
	}
}

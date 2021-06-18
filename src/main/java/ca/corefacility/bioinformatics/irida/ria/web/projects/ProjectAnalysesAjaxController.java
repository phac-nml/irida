package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;

/**
 * Controller for handling all ajax requests for Project Analyses Outputs.
 */
@RestController
@RequestMapping("/ajax/projects/analyses-outputs")
public class ProjectAnalysesAjaxController {

	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	public ProjectAnalysesAjaxController(AnalysisSubmissionService analysisSubmissionService) {
		this.analysisSubmissionService = analysisSubmissionService;
	}

	@GetMapping("/shared")
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getSharedSingleSampleOutputs(@RequestParam Long projectId) {
		return ResponseEntity.ok(
				analysisSubmissionService.getAllAnalysisOutputInfoSharedWithProject(projectId));
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

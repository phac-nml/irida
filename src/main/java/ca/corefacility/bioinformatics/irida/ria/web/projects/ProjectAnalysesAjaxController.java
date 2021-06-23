package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Controller for handling all ajax requests for Project Single Sample Analyses Outputs.
 */
@RestController
@RequestMapping("/ajax/projects/analyses-outputs")
public class ProjectAnalysesAjaxController {

	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;

	@Autowired
	public ProjectAnalysesAjaxController(AnalysisSubmissionService analysisSubmissionService, IridaWorkflowsService workflowsService) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = workflowsService;
	}

	/**
	 * Get all the shared single sample analysis outputs for the project
	 *
	 * @param projectId {@link ca.corefacility.bioinformatics.irida.model.project.Project} id
	 * @return a response containing a list of filtered {@link ProjectSampleAnalysisOutputInfo} objects
	 */
	@GetMapping("/shared")
	public ResponseEntity<List<ProjectSampleAnalysisOutputInfo>> getSharedSingleSampleOutputs(
			@RequestParam Long projectId) {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = analysisSubmissionService.getAllAnalysisOutputInfoSharedWithProject(
				projectId);
		Map<Long, Long> singleSampleCountMap = projectSampleAnalysisOutputInfos.stream()
				.collect(Collectors.groupingBy(s -> s.getAnalysisOutputFileId(), Collectors.counting()));

		// Filter out the projectSampleAnalysisOutputInfos list to only contain objects which are single sample analysis outputs
		List<ProjectSampleAnalysisOutputInfo> filterProjectSampleAnalysisOutputInfo = projectSampleAnalysisOutputInfos.stream()
				.filter(s -> singleSampleCountMap.get(s.getAnalysisOutputFileId()) == 1L)
				.collect(Collectors.toList());

		// Get the Irida workflow description and set it for each of the filtered analysis outputs
		filterProjectSampleAnalysisOutputInfo.forEach(s -> s.setWorkflowDescription(workflowsService.getIridaWorkflowOrUnknown(s.getWorkflowId())
				.getWorkflowDescription()));

		return ResponseEntity.ok(filterProjectSampleAnalysisOutputInfo);
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

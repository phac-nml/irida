package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * UI Service for all things related to project single sample analysis outputs.
 */
@Component
public class UIProjectAnalysesService {

	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;

	@Autowired
	public UIProjectAnalysesService(AnalysisSubmissionService analysisSubmissionService, IridaWorkflowsService workflowsService) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = workflowsService;
	}

	/**
	 * Get all the shared single sample analysis outputs for the project
	 *
	 * @param projectId {@link ca.corefacility.bioinformatics.irida.model.project.Project} id
	 * @return a list of filtered {@link ProjectSampleAnalysisOutputInfo} single sample analysis outputs
	 */
	public List<ProjectSampleAnalysisOutputInfo> getSharedSingleSampleOutputs(Long projectId) {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = analysisSubmissionService.getAllAnalysisOutputInfoSharedWithProject(
				projectId);
		return getSingleSampleAnalysisOutputsInfo(projectSampleAnalysisOutputInfos);
	}

	/**
	 * Get all the automated single sample analysis outputs for the project
	 *
	 * @param projectId {@link ca.corefacility.bioinformatics.irida.model.project.Project} id
	 * @return a list of filtered {@link ProjectSampleAnalysisOutputInfo} single sample analysis outputs
	 */
	public List<ProjectSampleAnalysisOutputInfo> getAutomatedSingleSampleOutputs(Long projectId) {
		List<ProjectSampleAnalysisOutputInfo> projectSampleAnalysisOutputInfos = analysisSubmissionService.getAllAutomatedAnalysisOutputInfoForAProject(
				projectId);
		return getSingleSampleAnalysisOutputsInfo(projectSampleAnalysisOutputInfos);
	}

	/**
	 * Utility method to get the single sample analysis outputs for the project
	 *
	 * @param outputs List of unfiltered {@link ProjectSampleAnalysisOutputInfo} single sample analysis outputs
	 * @return a list of filtered {@link ProjectSampleAnalysisOutputInfo} single sample analysis outputs
	 */
	private List<ProjectSampleAnalysisOutputInfo> getSingleSampleAnalysisOutputsInfo(List<ProjectSampleAnalysisOutputInfo> outputs) {
		Map<Long, Long> singleSampleCountMap = outputs.stream()
				.collect(Collectors.groupingBy(s -> s.getAnalysisOutputFileId(), Collectors.counting()));

		// Filter out the projectSampleAnalysisOutputInfos list to only contain objects which are single sample analysis outputs
		List<ProjectSampleAnalysisOutputInfo> filterProjectSampleAnalysisOutputInfo = outputs.stream()
				.filter(s -> singleSampleCountMap.get(s.getAnalysisOutputFileId()) == 1L)
				.collect(Collectors.toList());

		// Get the Irida workflow description and set it for each of the filtered analysis outputs
		filterProjectSampleAnalysisOutputInfo.forEach(s -> s.setWorkflowDescription(workflowsService.getIridaWorkflowOrUnknown(s.getWorkflowId())
				.getWorkflowDescription()));

		return filterProjectSampleAnalysisOutputInfo;
	}

}

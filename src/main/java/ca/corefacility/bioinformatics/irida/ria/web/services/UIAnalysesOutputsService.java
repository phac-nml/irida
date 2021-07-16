package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.components.AnalysisOutputFileDownloadManager;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jsonldjava.shaded.com.google.common.base.Strings;

/**
 * UI Service for all things related to project single sample analysis outputs.
 */
@Component
@Scope("session")
public class UIAnalysesOutputsService {

	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private UserService userService;
	private AnalysisOutputFileDownloadManager analysisOutputFileDownloadManager;

	@Autowired
	public UIAnalysesOutputsService(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService workflowsService, UserService userService,
			AnalysisOutputFileDownloadManager analysisOutputFileDownloadManager) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = workflowsService;
		this.userService = userService;
		this.analysisOutputFileDownloadManager = analysisOutputFileDownloadManager;
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
	 * Get all the automated single sample analysis outputs for the project
	 *
	 * @param principal Currently logged in user.
	 * @return a list of filtered {@link ProjectSampleAnalysisOutputInfo} single sample analysis outputs for the user
	 */
	public List<ProjectSampleAnalysisOutputInfo> getUserSingleSampleOutputs(Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		List<ProjectSampleAnalysisOutputInfo> userProjectSampleAnalysisOutputInfos = getSingleSampleAnalysisOutputsInfo(
				analysisSubmissionService.getAllUserAnalysisOutputInfo(user));

		// Need to set the user information to the currently logged in user
		userProjectSampleAnalysisOutputInfos.forEach(singleSampleAnalysisOutput -> {
			singleSampleAnalysisOutput.setUserId(user.getId());
			singleSampleAnalysisOutput.setUserFirstName(user.getFirstName());
			singleSampleAnalysisOutput.setUserLastName(user.getLastName());
		});
		return userProjectSampleAnalysisOutputInfos;
	}

	/**
	 * Prepare the download of multiple {@link AnalysisOutputFile} by adding them to a selection.
	 *
	 * @param outputs  Info for {@link AnalysisOutputFile} to download
	 * @param response {@link HttpServletResponse}
	 */
	public void prepareAnalysisOutputsSelectionDownload(List<ProjectSampleAnalysisOutputInfo> outputs,
			HttpServletResponse response) {
		analysisOutputFileDownloadManager.setSelection(outputs);
		response.setStatus(HttpServletResponse.SC_CREATED);
	}

	/**
	 * Download the selected {@link AnalysisOutputFile}.
	 *
	 * @param filename Filename for file download.
	 * @param response {@link HttpServletResponse}
	 */
	public void downloadAnalysisOutputsSelection(String filename, HttpServletResponse response) {
		Map<ProjectSampleAnalysisOutputInfo, AnalysisOutputFile> files = analysisOutputFileDownloadManager.getSelection();
		FileUtilities.createBatchAnalysisOutputFileZippedResponse(response, filename, files);
	}

	/**
	 * Download analysis output file
	 *
	 * @param analysisSubmissionId Id for a {@link AnalysisSubmission}
	 * @param fileId               the id of the file to download
	 * @param filename             Optional filename for file download.
	 * @param response             {@link HttpServletResponse}
	 */
	public void downloadIndividualAnalysisOutputFile(Long analysisSubmissionId, Long fileId, String filename,
			HttpServletResponse response) {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(analysisSubmissionId);

		Analysis analysis = analysisSubmission.getAnalysis();
		Set<AnalysisOutputFile> files = analysis.getAnalysisOutputFiles();

		Optional<AnalysisOutputFile> optFile = files.stream()
				.filter(f -> f.getId()
						.equals(fileId))
				.findAny();
		if (!optFile.isPresent()) {
			throw new EntityNotFoundException("Could not find file with id " + fileId);
		}

		if (!Strings.isNullOrEmpty(filename)) {
			FileUtilities.createSingleFileResponse(response, optFile.get(), filename);
		} else {
			FileUtilities.createSingleFileResponse(response, optFile.get());
		}
	}

	/**
	 * Utility method to get the single sample analysis outputs
	 *
	 * @param outputs List of unfiltered {@link ProjectSampleAnalysisOutputInfo} single sample analysis outputs
	 * @return a list of filtered {@link ProjectSampleAnalysisOutputInfo} single sample analysis outputs
	 */
	private List<ProjectSampleAnalysisOutputInfo> getSingleSampleAnalysisOutputsInfo(
			List<ProjectSampleAnalysisOutputInfo> outputs) {
		Map<Long, Long> singleSampleCountMap = outputs.stream()
				.collect(Collectors.groupingBy(s -> s.getAnalysisOutputFileId(), Collectors.counting()));

		// Filter out the projectSampleAnalysisOutputInfos list to only contain objects which are single sample analysis outputs
		List<ProjectSampleAnalysisOutputInfo> filterProjectSampleAnalysisOutputInfo = outputs.stream()
				.filter(s -> singleSampleCountMap.get(s.getAnalysisOutputFileId()) == 1L)
				.collect(Collectors.toList());

		// Get the Irida workflow description and set it for each of the filtered analysis outputs
		filterProjectSampleAnalysisOutputInfo.forEach(s -> s.setWorkflowDescription(
				workflowsService.getIridaWorkflowOrUnknown(s.getWorkflowId())
						.getWorkflowDescription()));

		return filterProjectSampleAnalysisOutputInfo;
	}
}

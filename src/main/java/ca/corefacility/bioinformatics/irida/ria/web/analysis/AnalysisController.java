package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Controller for Analysis.
 */
@Controller
@RequestMapping("/analysis")
public class AnalysisController {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);
	// PAGES
	public static final Map<AnalysisType, String> PREVIEWS = ImmutableMap.of(AnalysisType.PHYLOGENOMICS, "tree");
	private static final String REDIRECT_ERROR = "redirect:errors/not_found";
	private static final String BASE = "analysis/";
	public static final String PAGE_DETAILS_DIRECTORY = BASE + "details/";
	public static final String PAGE_USER_ANALYSIS = BASE + "analyses";
	public static final String PREVIEW_UNAVAILABLE = PAGE_DETAILS_DIRECTORY + "unavailable";

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
	}

	// ************************************************************************************************
	// PAGES
	// ************************************************************************************************

	@RequestMapping("/list")
	public String getUserAnalysesPage(Model model, Locale locale) {
		String response = PAGE_USER_ANALYSIS;
		try {
			generateAnalysesPageModel(false, model, locale);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Workflow not found - See stack:", e);
			response = REDIRECT_ERROR;
		}
		return response;
	}

	@RequestMapping("/list/all")
	public String getAdminAnalysesPage(Model model, Locale locale) {
		String response = PAGE_USER_ANALYSIS;
		try {
			generateAnalysesPageModel(true, model, locale);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Workflow not found - See stack:", e);
			response = REDIRECT_ERROR;
		}
		return response;
	}
	
	/**
	 * View details about an individual analysis submission
	 * 
	 * @param submissionId
	 *            the ID of the submission
	 * @param model
	 *            Model for the view
	 * @param locale
	 *            User's locale
	 * @return name of the details page view
	 */
	@RequestMapping(value = "/{submissionId}", produces = MediaType.TEXT_HTML_VALUE)
	public String getDetailsPage(@PathVariable Long submissionId, Model model, Locale locale) {
		logger.trace("reading analysis submission " + submissionId);
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		model.addAttribute("analysisSubmission", submission);

		UUID workflowUUID = submission.getWorkflowId();
		logger.trace("Workflow ID is " + workflowUUID);

		IridaWorkflow iridaWorkflow;
		try {
			iridaWorkflow = workflowsService.getIridaWorkflow(workflowUUID);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Error finding workflow, ", e);
			throw new EntityNotFoundException("Couldn't find workflow for submission " + submission.getId(), e);
		}

		// Get the name of the workflow
		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription().getAnalysisType();
		String viewName = getViewForAnalysisType(analysisType);
		String workflowName = messageSource.getMessage("workflow." + analysisType.toString() + ".title", null, locale);
		model.addAttribute("workflowName", workflowName);

		// Input files
		// - Paired
		Set<SequenceFilePair> inputFilePairs = submission.getPairedInputFiles();
		model.addAttribute("inputFilePairs", inputFilePairs);

		/*
		 * Preview information
		 */
		try {
			if (submission.getAnalysisState().equals(AnalysisState.COMPLETED)) {
				if (analysisType.equals(AnalysisType.PHYLOGENOMICS)) {
					tree(submission, model);
				}
			}

		} catch (IOException e) {
			logger.error("Couldn't get preview for analysis", e);
		} 

		return viewName;
	}

	
	// ************************************************************************************************
	// Analysis view setup
	// ************************************************************************************************

	/**
	 * Construct the model parameters for an
	 * {@link AnalysisPhylogenomicsPipeline}
	 * 
	 * @param submission
	 *            The analysis submission
	 * @param model
	 *            The model to add parameters
	 * @throws IOException
	 *             If the tree file couldn't be read
	 */
	private void tree(AnalysisSubmission submission, Model model) throws IOException {
		AnalysisPhylogenomicsPipeline analysis = (AnalysisPhylogenomicsPipeline) submission.getAnalysis();
		AnalysisOutputFile file = analysis.getPhylogeneticTree();
		List<String> lines = Files.readAllLines(file.getFile());
		model.addAttribute("analysis", analysis);
		model.addAttribute("newick", lines.get(0));

		// inform the view to display the tree preview
		model.addAttribute("preview", "tree");
	}

	/**
	 * Get a list of analyses either for a user or an administrator
	 *
	 * @param all
	 * 		{@link boolean} whether or not to show all the system analysis or just the users.
	 * @param locale
	 * 		{@link Locale} locale for the current user.
	 * @param httpServletResponse
	 * 		{@link HttpServletResponse} needed in case of error to update the response.
	 *
	 * @return A JSON object containing the analyses.
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxGetAnalysesListForUser(@RequestParam boolean all,
			Locale locale, HttpServletResponse httpServletResponse) {
		Map<String, Object> response = new HashMap<>();

		Set<AnalysisSubmission> analyses;
		if (all) {
			analyses = new HashSet<>(
					(Collection<? extends AnalysisSubmission>) analysisSubmissionService.findAll());
		} else {
			analyses = analysisSubmissionService.getAnalysisSubmissionsForCurrentUser();
		}

		List<Map<String, String>> analysesMap = new ArrayList<>();
		try {
			for (AnalysisSubmission sub : analyses) {
				String remoteAnalysisId = sub.getRemoteAnalysisId();
				UUID workflowUUID = sub.getWorkflowId();
				String type = workflowsService.getIridaWorkflow(workflowUUID).getWorkflowDescription().getAnalysisType()
						.toString();
				String workflowName = messageSource.getMessage("workflow." + type + ".title", null, locale);

				String analysisState = sub.getAnalysisState().toString();

				Map<String, String> map = new HashMap<>();
				map.put("id", sub.getId().toString());
				map.put("label", sub.getLabel());
				map.put("workflowId", sub.getWorkflowId().toString());
				map.put("workflowName", workflowName);
				map.put("remoteAnalysisId", Strings.isNullOrEmpty(remoteAnalysisId) ? "NOT SET" : remoteAnalysisId);
				map.put("state", messageSource.getMessage("analysis.state." + analysisState, null, locale));
				map.put("analysisState", analysisState.toUpperCase());
				map.put("createdDate", String.valueOf(sub.getCreatedDate().getTime()));
				map.put("submitter", sub.getSubmitter().getLabel());

				if (sub.getAnalysisState().equals(AnalysisState.COMPLETED)) {
					Analysis analysis = sub.getAnalysis();
					long duration = sub.getCreatedDate().getTime() - analysis.getCreatedDate().getTime();
					map.put("duration", String.valueOf(Math.abs(duration)));
				}

				if (!sub.getAnalysisState().equals(AnalysisState.ERROR)) {
					float percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(
							sub.getId());
					map.put("percentComplete", Float.toString(percentComplete));
				}

				analysesMap.add(map);
			}
			response.put("analyses", analysesMap);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Error finding workflow, ", e);
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.put("error", ImmutableMap.of("url", "errors/not_found"));
		} catch (NoPercentageCompleteException e) {
			logger.error("Error getting percent complete.", e);
		} catch (ExecutionManagerException e) {
			logger.error("ExecutionManagerException error", e);
		}
		return response;
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	/**
	 * Download all output files from an {@link AnalysisSubmission}
	 *
	 * @param analysisSubmissionId
	 * 		Id for a {@link AnalysisSubmission}
	 * @param response
	 * 		{@link HttpServletResponse}
	 *
	 * @throws IOException
	 * 		if we fail to create a zip file.
	 */
	@RequestMapping(value = "/ajax/download/{analysisSubmissionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public void getAjaxDownloadAnalysisSubmission(@PathVariable Long analysisSubmissionId, HttpServletResponse response)
			throws IOException {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(analysisSubmissionId);
		Analysis analysis = analysisSubmission.getAnalysis();
		Set<AnalysisOutputFile> files = analysis.getAnalysisOutputFiles();
		FileUtilities.createAnalysisOutputFileZippedResponse(response, analysisSubmission.getName(), files);
	}

	/**
	 * Get the current status for a given {@link AnalysisSubmission}
	 *
	 * @param submissionId
	 * 		The {@link UUID} id for a given {@link AnalysisSubmission}
	 * @param locale
	 * 		The users current {@link Locale}
	 *
	 * @return {@link HashMap} containing the status and the percent complete for the {@link AnalysisSubmission}
	 */
	@RequestMapping(value = "/ajax/status/{submissionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, String> getAjaxStatusUpdateForAnalysisSubmission(@PathVariable Long submissionId,
			Locale locale) {
		Map<String, String> result = new HashMap<>();
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(submissionId);
		AnalysisState state = analysisSubmission.getAnalysisState();
		result.put("state", state.toString());
		result.put("stateLang", messageSource.getMessage("analysis.state." + state.toString(), null, locale));
		if (!state.equals(AnalysisState.ERROR)) {
			float percentComplete = 0;
			try {
				percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(
						analysisSubmission.getId());
				result.put("percentComplete", Float.toString(percentComplete));
			} catch (ExecutionManagerException e) {
				logger.error("Error getting the percentage complete", e);
				result.put("percentageComplete", "");
			}
		}
		return result;
	}

	/**
	 * Generate the model for the analyses page.
	 *
	 * @param isAdmin
	 * 		If the user is an administrator and want all the analyses.
	 * @param model
	 * 		{@link Model} for the current view.
	 * @param locale
	 * 		{@link Locale} for the current user.
	 *
	 * @throws IridaWorkflowNotFoundException
	 */
	private void generateAnalysesPageModel(boolean isAdmin, Model model, Locale locale)
			throws IridaWorkflowNotFoundException {
		model.addAttribute("admin", isAdmin);
		model.addAttribute("workflows", getAnalysisWorkflowTypes(locale));
		model.addAttribute("states", AnalysisState.values());
	}

	/**
	 * Utility method to get a list of the system Workflows.
	 *
	 * @param locale
	 * 		{@link Locale} for the current user.
	 *
	 * @return {@link List} containing the workflows names and ids.
	 * @throws IridaWorkflowNotFoundException
	 */
	private List<Map<String, String>> getAnalysisWorkflowTypes(Locale locale) throws IridaWorkflowNotFoundException {
		Set<AnalysisType> workflows = workflowsService.getRegisteredWorkflowTypes();
		List<Map<String, String>> flows = new ArrayList<>(workflows.size());
		for (AnalysisType type : workflows) {
			IridaWorkflow flow = workflowsService.getDefaultWorkflowByType(type);
			IridaWorkflowDescription description = flow.getWorkflowDescription();
			String name = type.toString();
			String key = "workflow." + name;
			flows.add(ImmutableMap.of(
					"id", description.getId().toString(),
					"value", name,
					"title",
					messageSource
							.getMessage(key + ".title", null, locale)
			));
		}
		return flows;
	}

	/**
	 * Get the view name for different analysis types
	 *
	 * @param type
	 * 		The {@link AnalysisType}
	 *
	 * @return the view name to display
	 */
	private String getViewForAnalysisType(AnalysisType type) {
		String viewName = null;
		if (PREVIEWS.containsKey(type)) {
			viewName = PAGE_DETAILS_DIRECTORY + PREVIEWS.get(type);
		} else {
			viewName = PREVIEW_UNAVAILABLE;
		}

		return viewName;
	}
}

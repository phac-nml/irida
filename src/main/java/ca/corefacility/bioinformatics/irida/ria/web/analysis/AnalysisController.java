package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Controller for Analysis.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/analysis")
public class AnalysisController {
	private static final Map<String, String> ANALYSIS_TYPE_NAMES = ImmutableMap.of("1",
			"Whole Genome Phylogenomics Pipeline");
	private static final Map<Class<? extends AnalysisSubmission>, String> ANALYSIS_TYPE_IDS = ImmutableMap.of(
			AnalysisSubmission.class, "1");
	private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);

	// PAGES
	private static final String REDIRECT_ERROR = "redirect:errors/not_found";
	private static final String BASE = "analysis/";
	public static final String PAGE_ADMIN_ANALYSIS = BASE + "admin";
	public static final String PAGE_USER_ANALYSIS = BASE + "analysis_user";
	public static final String PAGE_TREE_ANALYSIS_PREVIEW = BASE + "preview/tree";

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionService;
	private UserService userService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService,
			UserService userService, IridaWorkflowsService iridaWorkflowsService, MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.userService = userService;
		this.workflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
	}

	// ************************************************************************************************
	// PAGES
	// ************************************************************************************************

	/**
	 * Get the Analysis Admin Page
	 *
	 * @return uri for the analysis admin page
	 */

	@RequestMapping("/admin")
	public String getPageAdminAnalysis(Model model) {
		logger.trace("Showing the Analysis Admin Page");
		// TODO: (14-08-29 - Josh) Once individuals can own an analysis this
		// needs to be only admin.
		model.addAttribute("types", ANALYSIS_TYPE_NAMES);
		return PAGE_ADMIN_ANALYSIS;
	}

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
	public String getAdminAnalysesPage(Model model, Principal principal, Locale locale) {
		String response = PAGE_USER_ANALYSIS;
		User user = userService.getUserByUsername(principal.getName());
		boolean isAdmin = user.getSystemRole().equals(Role.ROLE_ADMIN);

		if (!isAdmin) {
			throw new AccessDeniedException("User does not have permission to see all analysis.");
		}

		try {
			generateAnalysesPageModel(true, model, locale);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Workflow not found - See stack:", e);
			response = REDIRECT_ERROR;
		}
		return response;
	}

	/**
	 * Get the page for previewing a tree result
	 *
	 * @param analysisId
	 * 		Id for the {@link AnalysisSubmission}
	 * @param model
	 * 		{@link Model}
	 *
	 * @return Name of the page
	 * @throws IOException
	 */
	@RequestMapping("/preview/tree/{analysisId}")
	public String getTreeAnalysis(@PathVariable Long analysisId, Model model) throws IOException {
		logger.trace("Getting the preview of the the tree");
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(analysisId);
		AnalysisPhylogenomicsPipeline analysis = (AnalysisPhylogenomicsPipeline) analysisSubmission.getAnalysis();
		AnalysisOutputFile file = analysis.getPhylogeneticTree();
		List<String> lines = Files.readAllLines(file.getFile());
		model.addAttribute("analysis", analysis);
		model.addAttribute("analysisSubmission", analysisSubmission);
		model.addAttribute("newick", lines.get(0));
		return PAGE_TREE_ANALYSIS_PREVIEW;
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	/**
	 * Get a list of analyses either for a user or an administrator
	 *
	 * @param all
	 * 		{@link boolean} whether or not to show all the system analysis or just the users.
	 * @param principal
	 * 		{@link Principal} the current user.
	 * @param locale
	 * 		{@link Locale} locale for the current user.
	 * @param httpServletResponse
	 * 		{@link HttpServletResponse} needed in case of error to update the response.
	 *
	 * @return A JSON object containing the analyses.
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxGetAnalysesListForUser(@RequestParam boolean all, Principal principal,
			Locale locale, HttpServletResponse httpServletResponse) {
		Map<String, Object> response = new HashMap<>();

		Set<AnalysisSubmission> analyses;
		if (all && userService.getUserByUsername(principal.getName()).getSystemRole().equals(Role.ROLE_ADMIN)) {
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

				if (sub.getAnalysisState().equals(AnalysisState.COMPLETED)) {
					Analysis analysis = sub.getAnalysis();
					long duration = sub.getCreatedDate().getTime() - analysis.getCreatedDate().getTime();
					map.put("duration", String.valueOf(Math.abs(duration)));
				}

				analysesMap.add(map);
			}
			response.put("analyses", analysesMap);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Error finding workflow, ", e);
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.put("error", ImmutableMap.of("url", "errors/not_found"));
		}
		return response;
	}

	/**
	 * Download all output files from an {@link AnalysisSubmission}
	 *
	 * @param analysisSubmissionId
	 * 		Id for a {@link AnalysisSubmission}
	 * @param response
	 * 		{@link HttpServletResponse}
	 *
	 * @throws IOException
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
	 * @return {@link List} containting the workflows names and ids.
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
}

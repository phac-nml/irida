package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnalysisSubmissionSpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

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
	private static final String BASE = "analysis/";
	public static final String PAGE_ADMIN_ANALYSIS = BASE + "admin";
	public static final String PAGE_USER_ANALYSIS = BASE + "analysis_user";
	public static final String PAGE_TREE_ANALYSIS_PREVIEW = BASE + "preview/tree";

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionService;
	private UserService userService;
	private MessageSource messageSource;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService, UserService userService,
			MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.userService = userService;
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
	public String getPageUserAnalysis(Model model, Locale locale) {
		Map<String, String> stateMap = new HashMap<>();
		for (AnalysisState state : AnalysisState.values()) {
			stateMap.put(state.toString(),
					messageSource.getMessage("analysis.state." + state.toString(), null, locale));
		}
		model.addAttribute("states", stateMap);
		return PAGE_USER_ANALYSIS;
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
	 * Get a list of analysis by page and filter
	 *
	 * @param page
	 * 		Current page being displayed
	 * @param count
	 * 		Number of analysis per page
	 * @param sortedBy
	 * 		field to sort by
	 * @param sortDir
	 * 		direction to sort by
	 * @param state
	 * 		AnalysisSubmission state
	 * @param nameFilter
	 * 		text to filter the name by
	 * @param minDateFilter
	 * 		date to filter out anything previous
	 * @param maxDateFilter
	 * 		date to filter out anything after
	 *
	 * @return JSON object with analysis, total pages, and total analysis
	 * @throws IOException
	 */
	@RequestMapping(value = "/ajax/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAjaxListAllAnalysis(
			@RequestParam Integer page,
			@RequestParam Integer count,
			@RequestParam String sortedBy,
			@RequestParam String sortDir,
			@RequestParam(required = false) String state,
			@RequestParam(value = "name", required = false) String nameFilter,
			@RequestParam(value = "minDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date minDateFilter,
			@RequestParam(value = "maxDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date maxDateFilter)
			throws IOException {
		Map<String, Object> result = new HashMap<>();

		Sort.Direction order = sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

		// Let's see if we need to filter the state
		AnalysisState stateFilter = null;
		if (!Strings.isNullOrEmpty(state)) {
			StateFilter filter = new ObjectMapper().readValue(state, StateFilter.class);
			stateFilter = filter.getState();
		}

		Specification<AnalysisSubmission> specification = AnalysisSubmissionSpecification.searchAnalysis(nameFilter,
				stateFilter, minDateFilter, maxDateFilter);
		Page<AnalysisSubmission> analysisPage = analysisSubmissionService.search(specification, page, count, order,
				sortedBy);

		List<Map<String, String>> analysisList = new ArrayList<>();
		for (AnalysisSubmission analysisSubmission : analysisPage.getContent()) {
			String typeId = ANALYSIS_TYPE_IDS.get(analysisSubmission.getClass());
			String type = ANALYSIS_TYPE_NAMES.get(typeId);

			Map<String, String> map = new HashMap<>();
			map.put("id", analysisSubmission.getId().toString());
			map.put("name", analysisSubmission.getName());
			// TODO: (14-09-05- Josh) How to actual get this?
			map.put("type", type);
			map.put("typeId", typeId);
			map.put("status", analysisSubmission.getAnalysisState().toString());
			map.put("createdDate", String.valueOf(analysisSubmission.getCreatedDate().getTime()));
			analysisList.add(map);
		}

		result.put("analysis", analysisList);
		result.put("totalAnalysis", analysisPage.getTotalElements());
		result.put("totalPages", analysisPage.getTotalPages());
		return result;
	}

	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> ajaxGetAnalysesListForUser(Locale locale) {
		Set<AnalysisSubmission> analyses = analysisSubmissionService.getAnalysisSubmissionsForCurrentUser();
		List<Map<String, String>> analysesMap = new ArrayList<>();
		for (AnalysisSubmission sub : analyses) {
			String remoteAnalysisId = sub.getRemoteAnalysisId();
			String remoteWorkflowId = sub.getRemoteWorkflowId();
			String analysisState = sub.getAnalysisState().toString();

			Map<String, String> map = new HashMap<>();
			map.put("id", sub.getId().toString());
			map.put("label", sub.getLabel());
			map.put("workflowId", Strings.isNullOrEmpty(remoteWorkflowId) ? "NOT SET" : remoteWorkflowId);
			map.put("remoteAnalysisId", Strings.isNullOrEmpty(remoteAnalysisId) ? "NOT SET" : remoteAnalysisId);
			map.put("state", messageSource.getMessage("analysis.state." + analysisState, null, locale));
			map.put("analysisState", analysisState.toUpperCase());
			map.put("createdDate", String.valueOf(sub.getCreatedDate().getTime()));
			analysesMap.add(map);
		}
		return ImmutableMap.of("analyses", analysesMap);
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
}

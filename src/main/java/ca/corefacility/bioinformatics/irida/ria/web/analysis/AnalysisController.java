package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePairSnapshot;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisSISTRTyping;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnalysisSubmissionSpecification;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesUtils;
import ca.corefacility.bioinformatics.irida.security.permissions.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dandelion.datatables.core.ajax.ColumnDef;
import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
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
	public static final Map<AnalysisType, String> PREVIEWS = ImmutableMap
			.of(AnalysisType.PHYLOGENOMICS, "tree", AnalysisType.SISTR_TYPING, "sistr");
	private static final String BASE = "analysis/";
	public static final String PAGE_DETAILS_DIRECTORY = BASE + "details/";
	public static final String PREVIEW_UNAVAILABLE = PAGE_DETAILS_DIRECTORY + "unavailable";
	public static final String PAGE_ANALYSIS_LIST = BASE + "analysis-list";

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;
	private UserService userService;
	private ProjectService projectService;
	private SampleService sampleService;
	private UpdateAnalysisSubmissionPermission updateAnalysisPermission;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, UserService userService, ProjectService projectService, UpdateAnalysisSubmissionPermission updateAnalysisPermission,
			SampleService sampleService, MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
		this.userService = userService;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.updateAnalysisPermission = updateAnalysisPermission;
	}

	// ************************************************************************************************
	// PAGES
	// ************************************************************************************************

	/**
	 * Get the admin all {@link Analysis} list page
	 * 
	 * @param model
	 *            Model for view variables
	 * @return Name of the analysis page view
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("/all")
	public String getAdminAnalysisList(Model model) {
		model.addAttribute("userList", false);
		model.addAttribute("ajaxURL", "/analysis/ajax/list/all");
		model.addAttribute("states", AnalysisState.values());
		model.addAttribute("analysisTypes", workflowsService.getRegisteredWorkflowTypes());
		return PAGE_ANALYSIS_LIST;
	}

	/**
	 * Get the user {@link Analysis} list page
	 * 
	 * @param model
	 *            Model for view variables
	 * @return Name of the analysis page view
	 */
	@RequestMapping()
	public String getUserAnalysisList(Model model) {
		model.addAttribute("userList", true);
		model.addAttribute("ajaxURL", "/analysis/ajax/list");
		model.addAttribute("states", AnalysisState.values());
		model.addAttribute("analysisTypes", workflowsService.getRegisteredWorkflowTypes());
		return PAGE_ANALYSIS_LIST;
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
		model.addAttribute("version", iridaWorkflow.getWorkflowDescription().getVersion());

		// Input files
		// - Paired
		Set<SequenceFilePair> inputFilePairs = submission.getPairedInputFiles();
		model.addAttribute("paired_end", inputFilePairs);
		
		// - Remote
		Set<SequenceFilePairSnapshot> remoteFilesPaired = submission.getRemoteFilesPaired();
		model.addAttribute("remote_paired", remoteFilesPaired);
		
		// Check if user can update analysis
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("updatePermission", updateAnalysisPermission.isAllowed(authentication, submission));
		
		
		// Get the number of files currently being mirrored
		int mirroringCount = remoteFilesPaired.stream().mapToInt(p -> p.isMirrored() ? 0 : 1).sum();
		model.addAttribute("mirroringCount", mirroringCount);
		
		if (iridaWorkflow.getWorkflowDescription().requiresReference() && submission.getReferenceFile().isPresent()) {
			logger.debug("Adding reference file to page for submission with id [" + submission.getId() + "].");
			model.addAttribute("referenceFile", submission.getReferenceFile().get());
		} else {
			logger.debug("No reference file required for workflow.");
		}

		/*
		 * Preview information
		 */
		try {
			if (submission.getAnalysisState().equals(AnalysisState.COMPLETED)) {
				if (analysisType.equals(AnalysisType.PHYLOGENOMICS)) {
					tree(submission, model);
				} else if (analysisType.equals(AnalysisType.SISTR_TYPING)) {
					model.addAttribute("sistr", true);
				}
			}

		} catch (IOException e) {
			logger.error("Couldn't get preview for analysis", e);
		} 

		return viewName;
	}
	
	/**
	 * Get the status of projects that can be shared with the given analysis
	 * 
	 * @param submissionId
	 *            the {@link AnalysisSubmission} id
	 * @return a list of {@link SharedProjectResponse}
	 */
	@RequestMapping(value = "/ajax/{submissionId}/share", method = RequestMethod.GET)
	@ResponseBody
	public List<SharedProjectResponse> getSharedProjectsForAnalysis(@PathVariable Long submissionId) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		// Input files
		// - Paired
		Set<SequenceFilePair> inputFilePairs = submission.getPairedInputFiles();

		// get projects already shared with submission
		Set<Project> projectsShared = projectService.getProjectsForAnalysisSubmission(submission).stream()
				.map(ProjectAnalysisSubmissionJoin::getSubject).collect(Collectors.toSet());

		// get available projects
		Set<Project> projectsInAnalysis = projectService.getProjectsForSequencingObjects(inputFilePairs);

		List<SharedProjectResponse> projectResponses = projectsShared.stream()
				.map(p -> new SharedProjectResponse(p, true)).collect(Collectors.toList());

		// Create response for shared projects
		projectResponses.addAll(projectsInAnalysis.stream().filter(p -> !projectsShared.contains(p))
				.map(p -> new SharedProjectResponse(p, false)).collect(Collectors.toList()));
		
		projectResponses.sort(new Comparator<SharedProjectResponse>() {

			@Override
			public int compare(SharedProjectResponse p1, SharedProjectResponse p2) {
				return p1.getProject().getName().compareTo(p2.getProject().getName());
			}
		});

		return projectResponses;
	}
	
	/**
	 * Update the share status of a given {@link AnalysisSubmission} for a given
	 * {@link Project}
	 * 
	 * @param submissionId
	 *            the {@link AnalysisSubmission} id to share/unshare
	 * @param projectId
	 *            the {@link Project} id to share with
	 * @param shareStatus
	 *            whether or not to share the {@link AnalysisSubmission}
	 * @return Success message if successful
	 */
	@RequestMapping(value = "/ajax/{submissionId}/share", method = RequestMethod.POST)
	public Map<String, String> updateProjectShare(@PathVariable Long submissionId,
			@RequestParam("project") Long projectId,
			@RequestParam("shared") boolean shareStatus, Locale locale) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Project project = projectService.read(projectId);

		String message = "";
		if (shareStatus) {
			analysisSubmissionService.shareAnalysisSubmissionWithProject(submission, project);
			
			message = messageSource.getMessage("analysis.details.share.enable", new Object[] { project.getLabel() }, locale);
		} else {
			analysisSubmissionService.removeAnalysisProjectShare(submission, project);
			message = messageSource.getMessage("analysis.details.share.remove", new Object[] { project.getLabel() }, locale);
		}
		
		return ImmutableMap.of("result", "success", "message", message);
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
	 * Get the list of all {@link AnalysisSubmission}s in the system
	 * 
	 * @param criterias
	 *            {@link DatatablesCriterias} to filter or sort results
	 * @param locale
	 *            User's locale
	 * @return {@link DatatablesResponse} containing
	 *         {@link AnalysisTableResponse} objects
	 * @throws IridaWorkflowNotFoundException
	 *             If the requested workflow doesn't exist
	 * @throws NoPercentageCompleteException
	 *             If a percentage complete cannot be calculated
	 * @throws EntityNotFoundException
	 *             If the submission cannot be found
	 * @throws ExecutionManagerException
	 *             If the submission cannot be read properly
	 */
	@RequestMapping("/ajax/list/all")
	@ResponseBody
	public DatatablesResponse<AnalysisTableResponse> getSubmissions(@DatatablesParams DatatablesCriterias criterias,
			Locale locale) throws IridaWorkflowNotFoundException, NoPercentageCompleteException,
			EntityNotFoundException, ExecutionManagerException {
		int currentPage = DatatablesUtils.getCurrentPage(criterias);
		Map<String, Object> sortProps = DatatablesUtils.getSortProperties(criterias);
		String searchString = criterias.getSearch();

		Specification<AnalysisSubmission> filters = getFilters(searchString, criterias, null, null);

		Page<AnalysisSubmission> submissions = analysisSubmissionService.search(filters, currentPage,
				criterias.getLength(), (Sort.Direction) sortProps.get(DatatablesUtils.SORT_DIRECTION),
				(String) sortProps.get(DatatablesUtils.SORT_STRING));

		List<AnalysisTableResponse> responses = new ArrayList<>();
		for (AnalysisSubmission sub : submissions) {
			AnalysisTableResponse analysisTableResponse = new AnalysisTableResponse(sub, locale);
			responses.add(analysisTableResponse);
		}

		DataSet<AnalysisTableResponse> dataSet = new DataSet<>(responses, submissions.getTotalElements(),
				submissions.getTotalElements());

		return DatatablesResponse.build(dataSet, criterias);
	}
	
	/**
	 * Get the list of a users {@link AnalysisSubmission}s
	 * 
	 * @param criterias
	 *            {@link DatatablesCriterias} to filter or sort results
	 * @param principal
	 *            Logged in user
	 * @param locale
	 *            User's locale
	 * @return {@link DatatablesResponse} containing
	 *         {@link AnalysisTableResponse} objects
	 * @throws IridaWorkflowNotFoundException
	 *             If the requested workflow doesn't exist
	 * @throws NoPercentageCompleteException
	 *             If a percentage complete cannot be calculated
	 * @throws EntityNotFoundException
	 *             If the submission cannot be found
	 * @throws ExecutionManagerException
	 *             If the submission cannot be read properly
	 */
	@RequestMapping("/ajax/list")
	@ResponseBody
	public DatatablesResponse<AnalysisTableResponse> getSubmissionsForUser(
			@DatatablesParams DatatablesCriterias criterias, Principal principal, Locale locale)
			throws IridaWorkflowNotFoundException, NoPercentageCompleteException, EntityNotFoundException,
			ExecutionManagerException {
		User principalUser = userService.getUserByUsername(principal.getName());

		int currentPage = DatatablesUtils.getCurrentPage(criterias);
		Map<String, Object> sortProps = DatatablesUtils.getSortProperties(criterias);
		String searchString = criterias.getSearch();

		Specification<AnalysisSubmission> filters = getFilters(searchString, criterias, principalUser, null);

		Page<AnalysisSubmission> submissions = analysisSubmissionService.search(filters, currentPage,
				criterias.getLength(), (Sort.Direction) sortProps.get(DatatablesUtils.SORT_DIRECTION),
				(String) sortProps.get(DatatablesUtils.SORT_STRING));

		List<AnalysisTableResponse> responses = new ArrayList<>();
		for (AnalysisSubmission sub : submissions) {
			AnalysisTableResponse analysisTableResponse = new AnalysisTableResponse(sub, locale);
			responses.add(analysisTableResponse);
		}

		DataSet<AnalysisTableResponse> dataSet = new DataSet<>(responses, submissions.getTotalElements(),
				submissions.getTotalElements());

		return DatatablesResponse.build(dataSet, criterias);
	}
	
	@RequestMapping("/ajax/project/{projectId}/list")
	@ResponseBody
	public DatatablesResponse<AnalysisTableResponse> getSubmissionsForProject(
			@DatatablesParams DatatablesCriterias criterias, @PathVariable Long projectId, Principal principal, Locale locale)
			throws IridaWorkflowNotFoundException, NoPercentageCompleteException, EntityNotFoundException,
			ExecutionManagerException {

		Project project = projectService.read(projectId);
		
		int currentPage = DatatablesUtils.getCurrentPage(criterias);
		Map<String, Object> sortProps = DatatablesUtils.getSortProperties(criterias);
		String searchString = criterias.getSearch();

		Specification<AnalysisSubmission> filters = getFilters(searchString, criterias, null, project);

		Page<AnalysisSubmission> submissions = analysisSubmissionService.search(filters, currentPage,
				criterias.getLength(), (Sort.Direction) sortProps.get(DatatablesUtils.SORT_DIRECTION),
				(String) sortProps.get(DatatablesUtils.SORT_STRING));

		List<AnalysisTableResponse> responses = new ArrayList<>();
		for (AnalysisSubmission sub : submissions) {
			AnalysisTableResponse analysisTableResponse = new AnalysisTableResponse(sub, locale);
			responses.add(analysisTableResponse);
		}

		DataSet<AnalysisTableResponse> dataSet = new DataSet<>(responses, submissions.getTotalElements(),
				submissions.getTotalElements());

		return DatatablesResponse.build(dataSet, criterias);
	}

	@SuppressWarnings("resource")
	@RequestMapping("/ajax/sistr/{id}") @ResponseBody public Map<String,Object> getSistrAnalysis(@PathVariable Long id) {
		AnalysisSubmission submission = analysisSubmissionService.read(id);
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubimssion(submission);
		Map<String,Object> result = ImmutableMap.of("parse_results_error", true);
		
		// Get details about the workflow
		UUID workflowUUID = submission.getWorkflowId();
		IridaWorkflow iridaWorkflow;
		try {
			iridaWorkflow = workflowsService.getIridaWorkflow(workflowUUID);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Error finding workflow, ", e);
			throw new EntityNotFoundException("Couldn't find workflow for submission " + submission.getId(), e);
		}
		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription().getAnalysisType();
		if (analysisType.equals(AnalysisType.SISTR_TYPING)) {
			AnalysisSISTRTyping analysis = (AnalysisSISTRTyping) submission.getAnalysis();
			Path path = analysis.getSISTRResults().getFile();
			try {
				String json = new Scanner(new BufferedReader(new FileReader(path.toFile()))).useDelimiter("\\Z").next();
				
				// verify file is proper json file
				ObjectMapper mapper = new ObjectMapper();
				List<Map<String,Object>> sistrResults = mapper.readValue(json, new TypeReference<List<Map<String,String>>>(){});
				
				if (sistrResults.size() > 0) {
					// should only ever be one sample for these results
					if (samples.size() == 1) {
						Sample sample = samples.iterator().next();
						result = sistrResults.get(0);
						
						result.put("parse_results_error", false);
						
						result.put("sample_name", sample.getSampleName());
						result.put("sample_organism", sample.getOrganism());
					} else {
						logger.error("Invalid number of associated samles for submission " + submission);
					}
				} else {
					logger.error("SISTR results for file [" + path + "] are not correctly formatted");
				}
			} catch (FileNotFoundException e) {
				logger.error("File [" + path + "] not found",e);
			} catch (JsonParseException | JsonMappingException e) {
				logger.error("Error attempting to parse file [" + path + "] as JSON",e);
			} catch (IOException e) {
				logger.error("Error reading file [" + path + "]", e);
			}
		}
		return result;
	}

	/**
	 * Get a search specification for listing {@link AnalysisSubmission}s
	 * 
	 * @param searchString
	 *            The basic search string to search
	 * @param criterias
	 *            {@link DatatablesCriterias} sent from the view
	 * @param user
	 *            User to filter results by. Send null if user is not requried
	 * @return Specification to send to the repsoitory search method
	 * @throws IridaWorkflowNotFoundException
	 *             If the requested workflow dows not exist
	 */
	private Specification<AnalysisSubmission> getFilters(String searchString, DatatablesCriterias criterias, User user, Project project)
			throws IridaWorkflowNotFoundException {
		//properties to search
		String name = null;
		AnalysisState state = null;
		Set<UUID> workflowIds = null;
		
		//get the properties from the criterias
		for (ColumnDef def : criterias.getColumnDefs()) {
			String columnName = def.getName();
			if (!Strings.isNullOrEmpty(def.getSearch())) {

				if (columnName.equals("name")) {
					name = def.getSearch();
				} else if (columnName.equalsIgnoreCase("analysisState")) {
					state = AnalysisState.fromString(def.getSearch());
				} else if (columnName.equalsIgnoreCase("workflowId")) {
					//get the workflow from the workflow id string
					AnalysisType workflow = AnalysisType.fromString(def.getSearch());
					Set<IridaWorkflow> allWorkflowsByType = workflowsService.getAllWorkflowsByType(workflow);
					workflowIds = allWorkflowsByType.stream().map(IridaWorkflow::getWorkflowIdentifier)
							.collect(Collectors.toSet());
				}
			}

		}

		return AnalysisSubmissionSpecification.filterAnalyses(searchString, name, state, user, workflowIds, project);
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************
	
	/**
	 * Delete an {@link AnalysisSubmission} by id.
	 * 
	 * @param analysisSubmissionId
	 *            the submission ID to delete.
	 */
	@RequestMapping("/ajax/delete/{analysisSubmissionId}")
	@ResponseBody
	public Map<String, String> deleteAjaxAnalysisSubmission(@PathVariable Long analysisSubmissionId, final Locale locale) {
		final AnalysisSubmission deletedSubmission = analysisSubmissionService.read(analysisSubmissionId);
		analysisSubmissionService.delete(analysisSubmissionId);
		return ImmutableMap.of(
				"result", messageSource.getMessage("analysis.delete.message", new Object[]{ deletedSubmission.getLabel() }, locale)
		);
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
	
	/**
	 * Class holding the information to send to the client to display
	 * {@link AnalysisSubmission}s
	 */
	public class AnalysisTableResponse {
		private Long id;
		private String name;
		private User submitter;
		private AnalysisSubmission submission;
		private String workflowId;
		private String analysisState;
		private String duration;
		private String percentComplete;
		private Date createdDate;
		private boolean updatePermission;

		public AnalysisTableResponse(AnalysisSubmission submission, Locale locale)
				throws IridaWorkflowNotFoundException, NoPercentageCompleteException, EntityNotFoundException,
				ExecutionManagerException {
			this.submission = submission;

			this.id = submission.getId();
			this.name = submission.getName();
			this.submitter = submission.getSubmitter();
			this.createdDate = submission.getCreatedDate();

			// get the workflow name
			UUID workflowUUID = submission.getWorkflowId();
			String type = workflowsService.getIridaWorkflow(workflowUUID).getWorkflowDescription().getAnalysisType()
					.toString();
			workflowId = messageSource.getMessage("workflow." + type + ".title", null, locale);

			// get the analysis state message
			String analysisState = submission.getAnalysisState().toString();
			this.analysisState = messageSource.getMessage("analysis.state." + analysisState, null, locale);

			// get duration
			if (submission.getAnalysisState().equals(AnalysisState.COMPLETED)) {
				Analysis analysis = submission.getAnalysis();
				long dur = submission.getCreatedDate().getTime() - analysis.getCreatedDate().getTime();
				duration = String.valueOf(Math.abs(dur));
			}

			if (!submission.getAnalysisState().equals(AnalysisState.ERROR)) {
				float percentComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(submission
						.getId());
				this.percentComplete = Float.toString(percentComplete);
			}
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			updatePermission = updateAnalysisPermission.isAllowed(authentication, submission);
		}

		public AnalysisSubmission getSubmission() {
			return submission;
		}

		public String getWorkflowId() {
			return workflowId;
		}

		public String getAnalysisState() {
			return analysisState;
		}

		public String getDuration() {
			return duration;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public User getSubmitter() {
			return submitter;
		}

		public String getPercentComplete() {
			return percentComplete;
		}
		
		public Date getCreatedDate() {
			return createdDate;
		}
		
		public boolean getUpdatePermission() {
			return updatePermission;
		}
	}
	
	/**
	 * Response object storing a project and whether or not it's shared with a
	 * given {@link AnalysisSubmission}
	 */
	@SuppressWarnings("unused")
	private class SharedProjectResponse {
		private Project project;
		private boolean shared;

		public SharedProjectResponse(Project project, boolean shared) {
			this.project = project;
			this.shared = shared;
		}

		public Project getProject() {
			return project;
		}

		public boolean isShared() {
			return shared;
		}
	}
}

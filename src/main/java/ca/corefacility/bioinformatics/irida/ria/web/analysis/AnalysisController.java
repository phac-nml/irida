package ca.corefacility.bioinformatics.irida.ria.web.analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.AnalysesListingService;
import ca.corefacility.bioinformatics.irida.security.permissions.analysis.UpdateAnalysisSubmissionPermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	public static final String PAGE_ANALYSIS_LIST = "analyses/analyses";

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;
	private UserService userService;
	private ProjectService projectService;
	private UpdateAnalysisSubmissionPermission updateAnalysisPermission;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private SequencingObjectService sequencingObjectService;
	private AnalysesListingService analysesListingService;

	@Autowired
	public AnalysisController(AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, UserService userService, SampleService sampleService,
			ProjectService projectService, UpdateAnalysisSubmissionPermission updateAnalysisPermission,
			MetadataTemplateService metadataTemplateService, SequencingObjectService sequencingObjectService,
			AnalysesListingService analysesListingService, MessageSource messageSource) {
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;
		this.userService = userService;
		this.updateAnalysisPermission = updateAnalysisPermission;
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.metadataTemplateService = metadataTemplateService;
		this.sequencingObjectService = sequencingObjectService;
		this.analysesListingService = analysesListingService;
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
		model.addAttribute("analysisType", analysisType);
		String viewName = getViewForAnalysisType(analysisType);
		String workflowName = messageSource.getMessage("workflow." + analysisType.toString() + ".title", null, locale);
		model.addAttribute("workflowName", workflowName);
		model.addAttribute("version", iridaWorkflow.getWorkflowDescription().getVersion());

		// Input files
		// - Paired
		Set<SequenceFilePair> inputFilePairs = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(submission, SequenceFilePair.class);
		model.addAttribute("paired_end", inputFilePairs);

		// Check if user can update analysis
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("updatePermission", updateAnalysisPermission.isAllowed(authentication, submission));

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
				} else if (analysisType.equals(AnalysisType.SNV_SUBTYPING_COLLECTION)) {
					model.addAttribute("bio_hansel", true);
				}
			}

		} catch (IOException e) {
			logger.error("Couldn't get preview for analysis", e);
		}

		return viewName;
	}

	/**
	 * Update an analysis name
	 *
	 * @param submissionId ID of the submission to update
	 * @param name         name to update the analysis to
	 * @param model        model for view
	 * @param locale       locale of the user
	 * @return redirect to the analysis page after update
	 */
	@RequestMapping(value = "/{submissionId}/edit", produces = MediaType.TEXT_HTML_VALUE)
	public String editAnalysisName(@PathVariable Long submissionId, @RequestParam String name, Model model,
			Locale locale) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);

		submission.setName(name);

		boolean error = false;

		try {
			analysisSubmissionService.update(submission);
		} catch (Exception e) {
			logger.error("Error while updating analysis name", e);
			error = true;
		}

		if (error) {
			model.addAttribute("updateError", true);
			return getDetailsPage(submissionId, model, locale);
		}

		return "redirect:/analysis/" + submissionId;
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
		Set<SequenceFilePair> inputFilePairs = sequencingObjectService
				.getSequencingObjectsOfTypeForAnalysisSubmission(submission, SequenceFilePair.class);

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

	/**
	 * Get the page for viewing advanced phylogenetic visualization
	 *
	 * @param submissionId
	 * 		{@link Long} identifier for an {@link AnalysisSubmission}
	 * @param model
	 * 		{@link Model}
	 *
	 * @return {@link String} path to the page template.
	 */
	@RequestMapping("/{submissionId}/advanced-phylo")
	public String getAdvancedPhylogeneticVisualizationPage(
			@PathVariable Long submissionId, Model model){

		model.addAttribute("submissionId", submissionId);
		return BASE + "visualizations/phylocanvas-metadata";
	}


	// ************************************************************************************************
	// Analysis view setup
	// ************************************************************************************************

	/**
	 * Construct the model parameters for an {@link AnalysisType#PHYLOGENOMICS}
	 * {@link Analysis}
	 *
	 * @param submission
	 *            The analysis submission
	 * @param model
	 *            The model to add parameters
	 * @throws IOException
	 *             If the tree file couldn't be read
	 */
	private void tree(AnalysisSubmission submission, Model model) throws IOException {
		final String treeFileKey = "tree";

		Analysis analysis = submission.getAnalysis();
		AnalysisOutputFile file = analysis.getAnalysisOutputFile(treeFileKey);
		List<String> lines = Files.readAllLines(file.getFile());
		model.addAttribute("analysis", analysis);
		model.addAttribute("newick", lines.get(0));

		// inform the view to display the tree preview
		model.addAttribute("preview", "tree");
	}

	/**
	 * DataTables request handler for an Administrator listing all {@link AnalysisSubmission}
	 *
	 * @param params {@link DataTablesParams}
	 * @param locale {@link Locale}
	 * @return {@link DataTablesResponse}
	 * @throws IridaWorkflowNotFoundException If the requested workflow doesn't exist
	 * @throws EntityNotFoundException        If the submission cannot be found
	 * @throws ExecutionManagerException      If the submission cannot be read properly
	 */
	@RequestMapping(value = "/ajax/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DataTablesResponse getSubmissions(@DataTablesRequest DataTablesParams params, Locale locale)
			throws IridaWorkflowNotFoundException, EntityNotFoundException, ExecutionManagerException {
		return analysesListingService.getPagedSubmissions(params, locale, null, null);
	}

	/**
	 * DataTables request handler for a User listing all {@link AnalysisSubmission}
	 *
	 * @param params {@link DataTablesParams}
	 * @param principal {@link Principal}
	 * @param locale {@link Locale}
	 * @return {@link DataTablesResponse}
	 * @throws IridaWorkflowNotFoundException If the requested workflow doesn't exist
	 * @throws EntityNotFoundException        If the submission cannot be found
	 * @throws ExecutionManagerException      If the submission cannot be read properly
	 */
	@RequestMapping("/ajax/list")
	@ResponseBody
	public DataTablesResponse getSubmissionsForUser(@DataTablesRequest DataTablesParams params, Principal principal,
			Locale locale) throws IridaWorkflowNotFoundException, EntityNotFoundException,
			ExecutionManagerException {
		User user = userService.getUserByUsername(principal.getName());
		return analysesListingService.getPagedSubmissions(params, locale, user, null);
	}

	/**
	 * DataTables request handler for a User listing all {@link AnalysisSubmission}
	 *
	 * @param params {@link DataTablesParams}
	 * @param projectId {@link Long}
	 * @param principal {@link Principal}
	 * @param locale {@link Locale}
	 * @return {@link DataTablesResponse}
	 * @throws IridaWorkflowNotFoundException If the requested workflow doesn't exist
	 * @throws EntityNotFoundException        If the submission cannot be found
	 * @throws ExecutionManagerException      If the submission cannot be read properly
	 */
	@RequestMapping("/ajax/project/{projectId}/list")
	@ResponseBody
	public DataTablesResponse getSubmissionsForProject(@DataTablesRequest DataTablesParams params,
			@PathVariable Long projectId, Principal principal, Locale locale)
			throws IridaWorkflowNotFoundException, NoPercentageCompleteException, EntityNotFoundException,
			ExecutionManagerException {
		Project project = projectService.read(projectId);
		return analysesListingService.getPagedSubmissions(params, locale, null, project);
	}

	@SuppressWarnings("resource")
	@RequestMapping("/ajax/sistr/{id}") @ResponseBody public Map<String,Object> getSistrAnalysis(@PathVariable Long id) {
		AnalysisSubmission submission = analysisSubmissionService.read(id);
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubimssion(submission);
		Map<String,Object> result = ImmutableMap.of("parse_results_error", true);

		final String sistrFileKey = "sistr-predictions";

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
			Analysis analysis = submission.getAnalysis();
			Path path = analysis.getAnalysisOutputFile(sistrFileKey).getFile();
			try {
				String json = new Scanner(new BufferedReader(new FileReader(path.toFile()))).useDelimiter("\\Z").next();

				// verify file is proper json file
				ObjectMapper mapper = new ObjectMapper();
				List<Map<String,Object>> sistrResults = mapper.readValue(json, new TypeReference<List<Map<String,Object>>>(){});

				if (sistrResults.size() > 0) {
					// should only ever be one sample for these results
					if (samples.size() == 1) {
						Sample sample = samples.iterator().next();
						result = sistrResults.get(0);

						result.put("parse_results_error", false);

						result.put("sample_name", sample.getSampleName());
					} else {
						logger.error("Invalid number of associated samples for submission " + submission);
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


	@SuppressWarnings("resource")
	@RequestMapping("/ajax/bio_hansel/{id}") @ResponseBody public Map<String,Object> getBioHanselAnalysis(@PathVariable Long id) {
		AnalysisSubmission submission = analysisSubmissionService.read(id);
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubimssion(submission);
        Map<String,Object> result = new HashMap<>();
        result.put("parse_results_error", true); //init with parsing error true

		final String[] hanselOutputFiles = {"hansel_results", "hansel_match_results", "technician_results"};

		// Get details about the workflow, to verify the correct Analysis Type.
		UUID workflowUUID = submission.getWorkflowId();
		IridaWorkflow iridaWorkflow;

		try {
			iridaWorkflow = workflowsService.getIridaWorkflow(workflowUUID);
		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Error finding workflow, ", e);
			throw new EntityNotFoundException("Couldn't find workflow for submission " + submission.getId(), e);
		}

		AnalysisType analysisType = iridaWorkflow.getWorkflowDescription().getAnalysisType();
		if (analysisType.equals(AnalysisType.SNV_SUBTYPING_COLLECTION)) {

			Analysis analysis = submission.getAnalysis();
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String,Object>> currResults;
			Path currPath;
			String currJson;
            boolean parseSuccess = true;

			for(String currFile : hanselOutputFiles){
			    currPath = analysis.getAnalysisOutputFile(currFile).getFile();

                try {
                    logger.debug("Attempting to parse "+currPath+" as JSON.");
                    currJson = new String(Files.readAllBytes(currPath));
                    currResults = mapper.readValue(currJson, new TypeReference<List<Map<String,Object>>>(){});

                    if( currResults.size() > 0 && currResults.size() == 1){
                        result.put(currFile, currResults.get(0));
                    }else{
                        logger.error(String.format("Invalid output file was detected from %s. Could not get results! ", currFile));
                        parseSuccess = false;
                    }

                } catch (JsonParseException | JsonMappingException e) {
                    logger.error("Error attempting to parse file [" + currPath + "] as JSON",e);
                } catch (FileNotFoundException e) {
                    logger.error("File [" + currPath + "] not found",e);
                } catch (IOException e) {
                    logger.error("Error reading file [" + currPath + "]", e);
                }

                if( parseSuccess ){
                    result.put("parse_results_error", false);
                }
            }

		}
		return result;
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
	 *            Id for a {@link AnalysisSubmission}
	 * @param response
	 *            {@link HttpServletResponse}
	 */
	@RequestMapping(value = "/ajax/download/{analysisSubmissionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public void getAjaxDownloadAnalysisSubmission(@PathVariable Long analysisSubmissionId,
			HttpServletResponse response) {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(analysisSubmissionId);

		Analysis analysis = analysisSubmission.getAnalysis();
		Set<AnalysisOutputFile> files = analysis.getAnalysisOutputFiles();
		FileUtilities.createAnalysisOutputFileZippedResponse(response, analysisSubmission.getName(), files);
	}

	/**
	 * Download single output files from an {@link AnalysisSubmission}
	 *
	 * @param analysisSubmissionId
	 *            Id for a {@link AnalysisSubmission}
	 * @param fileId
	 *            the id of the file to download
	 * @param response
	 *            {@link HttpServletResponse}
	 */
	@RequestMapping(value = "/ajax/download/{analysisSubmissionId}/file/{fileId}")
	public void getAjaxDownloadAnalysisSubmissionIndividualFile(@PathVariable Long analysisSubmissionId,
			@PathVariable Long fileId, HttpServletResponse response) {
		AnalysisSubmission analysisSubmission = analysisSubmissionService.read(analysisSubmissionId);

		Analysis analysis = analysisSubmission.getAnalysis();
		Set<AnalysisOutputFile> files = analysis.getAnalysisOutputFiles();

		Optional<AnalysisOutputFile> optFile = files.stream().filter(f -> f.getId().equals(fileId)).findAny();
		if (!optFile.isPresent()) {
			throw new EntityNotFoundException("Could not find file with id " + fileId);
		}

		FileUtilities.createSingleFileResponse(response, optFile.get());
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
	 * Get a newick file associated with a specific {@link AnalysisSubmission}.
	 *
	 * @param submissionId
	 * 		{@link Long} id for an {@link AnalysisSubmission}
	 *
	 * @return {@link Map} containing the newick file contents.
	 * @throws IOException
	 * 		{@link IOException} if the newick file is not found
	 */
	@RequestMapping("/ajax/{submissionId}/newick")
	@ResponseBody
	public Map<String, Object> getNewickForAnalysis(@PathVariable Long submissionId) throws IOException {
		final String treeFileKey = "tree";

		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Analysis analysis = submission.getAnalysis();
		AnalysisOutputFile file = analysis.getAnalysisOutputFile(treeFileKey);
		List<String> lines = Files.readAllLines(file.getFile());
		return ImmutableMap.of("newick", lines.get(0));
	}

	/**
	 * Get the metadata associated with a template for an analysis.
	 *
	 * @param submissionId
	 * 		{@link Long} identifier for the {@link AnalysisSubmission}
	 *
	 * @return {@link Map}
	 */
	@RequestMapping("/ajax/{submissionId}/metadata")
	@ResponseBody
	public Map<String, Object> getMetadataForAnalysisSamples(
			@PathVariable Long submissionId) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubimssion(submission);

		// Let's get a list of all the metadata available that is unique.
		Set<String> terms = new HashSet<>();
		for (Sample sample : samples) {
			if (!sample.getMetadata().isEmpty()) {
				Map<MetadataTemplateField, MetadataEntry> metadata = sample.getMetadata();
				terms.addAll(
						metadata.keySet().stream().map(MetadataTemplateField::getLabel).collect(Collectors.toSet()));
			}
		}

		// Get the metadata for the samples;
		Map<String, Object> metadata = new HashMap<>();
		for (Sample sample : samples) {
			Map<MetadataTemplateField, MetadataEntry> sampleMetadata = sample.getMetadata();
			Map<String,MetadataEntry> stringMetadata = new HashMap<>();
			sampleMetadata.entrySet().forEach(e -> {
				stringMetadata.put(e.getKey().getLabel(), e.getValue());
			});

			Map<String, MetadataEntry> valuesMap = new HashMap<>();
			for (String term : terms) {

				MetadataEntry value = stringMetadata.get(term);
				if (value == null) {
					// Not all samples will have the same metadata associated with it.  If a sample
					// is missing one of the terms, just give it an empty string.
					value = new MetadataEntry("", "text");
				}

				valuesMap.put(term, value);
			}
			metadata.put(sample.getLabel(), valuesMap);
		}

		return ImmutableMap.of(
				"terms", terms,
				"metadata", metadata
		);
	}

	/**
	 * Get a list of all {@link MetadataTemplate}s for the {@link AnalysisSubmission}
	 *
	 * @param submissionId id of the {@link AnalysisSubmission}
	 * @return a map of {@link MetadataTemplate}s
	 */
	@RequestMapping("/ajax/{submissionId}/metadata-templates")
	@ResponseBody
	public Map<String, Object> getMetadataTemplatesForAnalysis(@PathVariable Long submissionId) {
		AnalysisSubmission submission = analysisSubmissionService.read(submissionId);
		List<Project> projectsUsedInAnalysisSubmission = projectService.getProjectsUsedInAnalysisSubmission(submission);

		Set<Long> projectIds = new HashSet<>();
		Set<Map<String, Object>> templates = new HashSet<>();

		for (Project project : projectsUsedInAnalysisSubmission) {
			if (!projectIds.contains(project.getId())) {
				projectIds.add(project.getId());

				// Get the templates for the project
				List<ProjectMetadataTemplateJoin> templateList = metadataTemplateService
						.getMetadataTemplatesForProject(project);
				for (ProjectMetadataTemplateJoin projectMetadataTemplateJoin : templateList) {
					MetadataTemplate metadataTemplate = projectMetadataTemplateJoin.getObject();
					Map<String, Object> templateMap = ImmutableMap.of("label", metadataTemplate.getLabel(), "id",
							metadataTemplate.getId());
					templates.add(templateMap);
				}
			}
		}

		return ImmutableMap.of("templates", templates);
	}

	/**
	 * Generates a list of metadata fields for a five template.
	 *
	 * @param templateId
	 * 		{@link Long} id for the {@link MetadataTemplate} that the fields are required.
	 *
	 * @return {@link Map}
	 */
	@RequestMapping("/ajax/{submissionId}/metadata-template-fields")
	@ResponseBody
	public Map<String, Object> getMetadataTemplateFields(
			@RequestParam Long templateId){
		MetadataTemplate template = metadataTemplateService.read(templateId);
		List<MetadataTemplateField> metadataFields = template.getFields();
		List<String> fields = new ArrayList<>();
		for (MetadataTemplateField metadataField : metadataFields) {
			fields.add(metadataField.getLabel());
		}
		return ImmutableMap.of("fields", fields);
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

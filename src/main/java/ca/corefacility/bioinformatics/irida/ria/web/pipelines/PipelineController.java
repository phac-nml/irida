package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto.WorkflowParametersToSave;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFileRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Controller for pipeline related views
 *
 */
@Controller
@Scope("session")
@RequestMapping(PipelineController.BASE_URL)
public class PipelineController extends BaseController {
	// URI's
	public static final String BASE_URL = "/pipelines";
	/*
	 * CONSTANTS
	 */
	private static final String DEFAULT_WORKFLOW_PARAMETERS_ID = "default";
	private static final String CUSTOM_UNSAVED_WORKFLOW_PARAMETERS_ID = "custom";
	public static final String URL_EMPTY_CART_REDIRECT = "redirect:/pipelines";
	public static final String URL_LAUNCH = "pipelines/pipeline_selection";
	public static final String URL_GENERIC_PIPELINE = "pipelines/types/generic_pipeline";
	public static final String URI_LIST_PIPELINES = "/ajax/list.json";
	public static final String URI_AJAX_START_PIPELINE = "/ajax/start.json";
	public static final String URI_AJAX_CART_LIST = "/ajax/cart_list.json";
	// JSON KEYS
	public static final String JSON_KEY_SAMPLE_ID = "id";
	public static final String JSON_KEY_SAMPLE_OMIT_FILES_LIST = "omit";
	private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);
	/*
	 * SERVICES
	 */
	private ReferenceFileService referenceFileService;
	private SequenceFileService sequenceFileService;
	private SequenceFilePairService sequenceFilePairService;
	private AnalysisSubmissionService analysisSubmissionService;
	private ProjectService projectService;
	private UserService userService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;
	private final WorkflowNamedParametersService namedParameterService;
	
	private SequenceFileRemoteService sequenceFileRemoteService;
	/*
	 * CONTROLLERS
	 */
	private CartController cartController;

	@Autowired
	public PipelineController(SequenceFileService sequenceFileService,
			SequenceFilePairService sequenceFilePairService,
			ReferenceFileService referenceFileService,
			AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService,
			ProjectService projectService,
			UserService userService,
			SequenceFileRemoteService sequenceFileRemoteService,
			CartController cartController,
			MessageSource messageSource,
			final WorkflowNamedParametersService namedParameterService) {
		this.sequenceFileService = sequenceFileService;
		this.sequenceFilePairService = sequenceFilePairService;
		this.referenceFileService = referenceFileService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.projectService = projectService;
		this.userService = userService;
		this.cartController = cartController;
		this.messageSource = messageSource;
		this.namedParameterService = namedParameterService;
		this.sequenceFileRemoteService = sequenceFileRemoteService;
	}

	/**
	 * Get the Pipeline Selection Page
	 *
	 * @param model
	 * 		{@link Model}
	 * @param locale
	 * 		Current users {@link Locale}
	 *
	 * @return location of the pipeline selection page.
	 */
	@RequestMapping
	public String getPipelineLaunchPage(final Model model, Locale locale) {
		Set<AnalysisType> workflows = workflowsService.getRegisteredWorkflowTypes();

		List<Map<String, String>> flows = new ArrayList<>(workflows.size());
		workflows.stream().forEach(type -> {
			IridaWorkflow flow = null;
			try {
				flow = workflowsService.getDefaultWorkflowByType(type);
				IridaWorkflowDescription description = flow.getWorkflowDescription();
				String name = type.toString();
				String key = "workflow." + name;
				flows.add(ImmutableMap.of(
						"name", name,
						"id", description.getId().toString(),
						"title",
						messageSource
								.getMessage(key + ".title", null, locale),
						"description",
						messageSource
								.getMessage(key + ".description", null, locale)
				));
			} catch (IridaWorkflowNotFoundException e) {
				logger.error("Workflow not found - See stack:", e);
			}
		});

		flows.sort((f1, f2) -> f1.get("name").compareTo(f2.get("name")));
		model.addAttribute("counts", getCartSummaryMap());
		model.addAttribute("workflows", flows);
		return URL_LAUNCH;
	}

	/**
	 * Get a generic pipeline page.
	 * 
	 * @param model
	 *            the the model for the current request
	 * @param principal
	 *            the user in the current request
	 * @param locale
	 *            the locale that the user is using
	 * @param pipelineId
	 *            the pipeline to load
	 * @return a page reference or redirect to load.
	 */
	@RequestMapping(value = "/{pipelineId}")
	public String getSpecifiedPipelinePage(final Model model, Principal principal, Locale locale, @PathVariable UUID pipelineId) {
		String response = URL_EMPTY_CART_REDIRECT;

		Map<Project, Set<Sample>> cartMap = cartController.getSelected();
		Map<String, Sample> remoteSelected = cartController.getRemoteSelected();
		// Cannot run a pipeline on an empty cart!
		if (!cartMap.isEmpty() || !remoteSelected.isEmpty()) {

			IridaWorkflow flow = null;
			try {
				flow = workflowsService.getIridaWorkflow(pipelineId);
			} catch (IridaWorkflowNotFoundException e) {
				logger.error("Workflow not found - See stack:", e);
				return "redirect:errors/not_found";
			}

			User user = userService.getUserByUsername(principal.getName());
			// Get all the reference files that could be used for this pipeline.
			List<Map<String, Object>> referenceFileList = new ArrayList<>();
			List<Map<String, Object>> projectList = new ArrayList<>();
			List<Map<String, Object>> addRefList = new ArrayList<>();
			IridaWorkflowDescription description = flow.getWorkflowDescription();
			for (Project project : cartMap.keySet()) {
				// Check to see if it requires a reference file.
				if (description.requiresReference()) {
					List<Join<Project, ReferenceFile>> joinList = referenceFileService
							.getReferenceFilesForProject(project);
					for (Join<Project, ReferenceFile> join : joinList) {
						referenceFileList.add(ImmutableMap.of(
								"project", project,
								"file", join.getObject()
						));
					}

					if (referenceFileList.size() == 0) {
						if (user.getSystemRole().equals(Role.ROLE_ADMIN) || projectService
								.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER)) {
							addRefList.add(ImmutableMap.of(
									"name", project.getLabel(),
									"id", project.getId()
							));
						}
					}
				}

				Set<Sample> samples = cartMap.get(project);
				Map<String, Object> projectMap = new HashMap<>();
				List<Map<String, Object>> sampleList = new ArrayList<>();
				for (Sample sample : samples) {
					Map<String, Object> sampleMap = new HashMap<>();
					sampleMap.put("name", sample.getLabel());
					sampleMap.put("id", sample.getId().toString());
					Map<String, List<? extends Object>> files = new HashMap<>();

					// Paired end reads
					if (description.acceptsPairedSequenceFiles()) {
						files.put("paired_end", sequenceFilePairService.getSequenceFilePairsForSample(sample));
					}

					// Singe end reads
					if (description.acceptsSingleSequenceFiles()) {
						files.put("single_end", sequenceFileService.getUnpairedSequenceFilesForSample(sample));
					}

					sampleMap.put("files", files);
					sampleList.add(sampleMap);
				}

				projectMap.put("id", project.getId().toString());
				projectMap.put("name", project.getLabel());
				projectMap.put("samples", sampleList);
				projectList.add(projectMap);
			}
			
			List<Map<String, Object>> remoteSamples = new ArrayList<>();
			logger.debug("Getting remote files");
			for(String url : remoteSelected.keySet()){
				Sample sample = remoteSelected.get(url);
				Map<String, Object> sampleMap = new HashMap<>();
				sampleMap.put("name", sample.getLabel());
				sampleMap.put("id", sample.getSelfHref());
				Map<String, List<? extends Object>> files = new HashMap<>();
				
				if (description.acceptsPairedSequenceFiles()) {
					logger.debug("Getting pairs");
					files.put("paired_end", sequenceFileRemoteService.getSequenceFilePairsForSample(sample));
				}
				
				if (description.acceptsSingleSequenceFiles()) {
					logger.debug("Getting single files");
					files.put("single_end", sequenceFileRemoteService.getUnpairedSequenceFilesForSample(sample));
				}
				
				sampleMap.put("files", files);
				remoteSamples.add(sampleMap);
			}
			

			// Need to add the pipeline parameters
			final List<IridaWorkflowParameter> defaultWorkflowParameters = flow.getWorkflowDescription().getParameters();
			final List<Map<String, Object>> parameters = new ArrayList<>();
			if (defaultWorkflowParameters != null) {
				final List<Map<String, String>> defaultParameters = new ArrayList<>();
				final String workflowName = description.getName().toLowerCase();
				for (IridaWorkflowParameter p : defaultWorkflowParameters) {
					defaultParameters.add(ImmutableMap.of(
							"label",
							messageSource.getMessage("pipeline.parameters." + workflowName + "." + p.getName(), null, locale),
							"value", p.getDefaultValue(),
							"name", p.getName()
					));
				}
				parameters.add(ImmutableMap.of("id", DEFAULT_WORKFLOW_PARAMETERS_ID,
						"label", messageSource.getMessage("workflow.parameters.named.default", null, locale), "parameters", defaultParameters));
				final List<IridaWorkflowNamedParameters> namedParameters = namedParameterService.findNamedParametersForWorkflow(pipelineId);
				for (final IridaWorkflowNamedParameters p : namedParameters) {
					final List<Map<String, String>> namedParametersList = new ArrayList<>();
					for (final Map.Entry<String, String> parameter : p.getInputParameters().entrySet()) {
						namedParametersList.add(ImmutableMap.of(
							"label",
							messageSource.getMessage("pipeline.parameters." + workflowName + "." + parameter.getKey(), null, locale),
							"value", parameter.getValue(),
							"name", parameter.getKey()
					));
					}
					parameters.add(ImmutableMap.of("id", p.getId(), "label", p.getLabel(), "parameters", namedParametersList));
				}
				model.addAttribute("parameters", parameters);
				model.addAttribute("parameterModalTitle",
						messageSource.getMessage("pipeline.parameters.modal-title." + workflowName, null, locale));
			} else {
				model.addAttribute("noParameters", messageSource.getMessage("pipeline.no-parameters", null, locale));
			}

			model.addAttribute("title",
					messageSource.getMessage("pipeline.title." + description.getName(), null, locale));
			model.addAttribute("mainTitle",
					messageSource.getMessage("pipeline.h1." + description.getName(), null, locale));
			model.addAttribute("name", description.getName());
			model.addAttribute("pipelineId", pipelineId.toString());
			model.addAttribute("referenceFiles", referenceFileList);
			model.addAttribute("referenceRequired", description.requiresReference());
			model.addAttribute("addRefProjects", addRefList);
			model.addAttribute("projects", projectList);
			model.addAttribute("remoteSamples", remoteSamples);
			response = URL_GENERIC_PIPELINE;
		}

		return response;
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	/**
	 * Launch a pipeline
	 *
	 * @param locale
	 *            the locale that the browser is using for the current request.
	 * @param pipelineId
	 *            the id for the {@link IridaWorkflow}
	 * @param single
	 *            a list of {@link SequenceFile} id's
	 * @param paired
	 *            a list of {@link SequenceFilePair} id's
	 * @param parameters
	 *            TODO: This is a hack! Update when fixing issue #100
	 *            {@link Map} of ALL parameters passed. Only want the 'paras'
	 *            object: a {@link Map} of pipeline parameters
	 * @param ref
	 *            the id for a {@link ReferenceFile}
	 * @param name
	 *            a user provided name for the {@link IridaWorkflow}
	 *
	 * @return a JSON response with the status and any messages.
	 */
	@RequestMapping(value = "/ajax/start/{pipelineId}", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> ajaxStartPipeline(Locale locale,
			@PathVariable UUID pipelineId,
			@RequestParam(required = false) List<Long> single, @RequestParam(required = false) List<Long> paired,
			@RequestParam(required = false) Map<String, String> parameters,
			@RequestParam(required = false) Long ref, @RequestParam String name) {
		Map<String, Object> result = ImmutableMap.of("success", true);
		try {
			IridaWorkflow flow = workflowsService.getIridaWorkflow(pipelineId);
			IridaWorkflowDescription description = flow.getWorkflowDescription();

			// The pipeline needs to have a name.
			if (Strings.isNullOrEmpty(name)) {
				return ImmutableMap
						.of("error", messageSource.getMessage("workflow.no-name-provided", null, locale));
			}


			// Check to see if a reference file is required.
			if (description.requiresReference() && ref == null) {
				return ImmutableMap.of("error", messageSource.getMessage("pipeline.error.no-reference.pipeline-start", null, locale));
			}

			// Get a list of the files to submit
			List<SequenceFile> sequenceFiles = new ArrayList<>();
			List<SequenceFilePair> sequenceFilePairs = new ArrayList<>();

			if (single != null) {
				sequenceFiles = (List<SequenceFile>) sequenceFileService.readMultiple(single);
				// Check the single files for duplicates in a sample, throws SampleAnalysisDuplicateException
				sequenceFileService.getUniqueSamplesForSequenceFiles(Sets.newHashSet(sequenceFiles));
			}

			if (paired != null) {
				sequenceFilePairs = (List<SequenceFilePair>) sequenceFilePairService.readMultiple(paired);
				// Check the pair files for duplicates in a sample, throws SampleAnalysisDuplicateException
				sequenceFilePairService.getUniqueSamplesForSequenceFilePairs(Sets.newHashSet(sequenceFilePairs));
			}

			// Get the pipeline parameters
			Map<String, String> params = new HashMap<>();
			IridaWorkflowNamedParameters namedParameters = null;
			if (parameters.containsKey("selectedParameters")) {
				try {
					final Map<String, Object> passedParameters = extractPipelineParameters(parameters
							.get("selectedParameters"));
					// we should only have *one* parameter set supplied.
					final String selectedParametersId = passedParameters.get("id").toString();
					if (!DEFAULT_WORKFLOW_PARAMETERS_ID.equals(selectedParametersId)
							&& !CUSTOM_UNSAVED_WORKFLOW_PARAMETERS_ID.equals(selectedParametersId)) {
						// this means that a named parameter set was selected
						// and unmodified, so load up that named parameter set
						// to pass along.
						namedParameters = namedParameterService.read(Long.valueOf(selectedParametersId));
					} else {
						@SuppressWarnings("unchecked")
						final List<Map<String, String>> unnamedParameters = (List<Map<String, String>>) passedParameters.get("parameters");
						for (final Map<String, String> parameter : unnamedParameters) {
							params.put(parameter.get("name"), parameter.get("value"));
						}
					}
				} catch (final IOException e) {
					return ImmutableMap
							.of("parameterError", messageSource.getMessage("pipeline.parameters.error", null, locale));
				}
			}

			if (description.getInputs().requiresSingleSample()) {
				analysisSubmissionService.createSingleSampleSubmission(flow, ref, sequenceFiles, sequenceFilePairs,
						params, namedParameters, name);
			} else {
				analysisSubmissionService.createMultipleSampleSubmission(flow, ref, sequenceFiles, sequenceFilePairs,
						params, namedParameters, name);
			}

		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Cannot file IridaWorkflow [" + pipelineId + "]", e);
			result = ImmutableMap
					.of("pipelineError", messageSource.getMessage("pipeline.error.invalid-pipeline", null, locale));
		} catch (DuplicateSampleException e) {
			logger.error("Multiple files for Sample found", e);
			result = ImmutableMap.of("pipelineError", messageSource.getMessage("pipeline.error.duplicate-samples",
					null, locale));
		}

		return result;
	}
	
	/**
	 * Save a set of {@link IridaWorkflowNamedParameters} and respond with the
	 * ID that we saved the new set with.
	 * 
	 * @param params
	 *            the DTO with the parameters to save.
	 * @return a map with the ID of the saved named parameters.
	 */
	@RequestMapping(value = "/ajax/parameters", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> ajaxSaveParameters(@RequestBody final WorkflowParametersToSave params) {
		final IridaWorkflowNamedParameters namedParameters = namedParameterService.create(params.namedParameters());
		return ImmutableMap.of("id", namedParameters.getId());
	}

	/**
	 * Extract {@link IridaWorkflow} parameters from the request {@link Map}
	 *
	 * @param mapString
	 *            {@link Map} of parameters
	 *
	 * @return {@link Map} of parameters for the pipeline
	 * @throws IOException
	 *             when unable to parse the parameters from the provided string.
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> extractPipelineParameters(String mapString) throws IOException {
		// TODO [15-02-16] (Josh): Update when addressing issue #100
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(mapString, Map.class);
		} catch (IOException e) {
			logger.error("Error extracting parameters from submission", e);
			throw e;
		}
	}

	/**
	 * Get details about the contents of the cart.
	 *
	 * @return {@link Map} containing the counts of the projects and samples in the cart.
	 */
	private Map<String, Integer> getCartSummaryMap() {
		return ImmutableMap.of(
				"projects", cartController.getNumberOfProjects(),
				"samples", cartController.getNumberOfSamples()
		);
	}
}

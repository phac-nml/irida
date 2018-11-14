package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import ca.corefacility.bioinformatics.irida.model.workflow.description.*;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;

import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotDisplayableException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto.PipelineStartParameters;
import ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto.WorkflowParametersToSave;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

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
	private SequencingObjectService sequencingObjectService;
	private AnalysisSubmissionService analysisSubmissionService;
	private ProjectService projectService;
	private UserService userService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;
	private final WorkflowNamedParametersService namedParameterService;
	private UpdateSamplePermission updateSamplePermission;
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private GalaxyToolDataService galaxyToolDataService;
	
	/*
	 * CONTROLLERS
	 */
	private CartController cartController;
	
	/*
	 * Additional variables
	 */
	private String iridaPipelinePluginStyle;

	@Autowired
	public PipelineController(SequencingObjectService sequencingObjectService,
			ReferenceFileService referenceFileService, AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService, ProjectService projectService, UserService userService,
			CartController cartController, MessageSource messageSource,
			final WorkflowNamedParametersService namedParameterService,
			UpdateSamplePermission updateSamplePermission,
			AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor, GalaxyToolDataService galaxyToolDataService,
			@Qualifier("iridaPipelinePluginStyle") String iridaPipelinePluginStyle) {
		this.sequencingObjectService = sequencingObjectService;
		this.referenceFileService = referenceFileService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.projectService = projectService;
		this.userService = userService;
		this.cartController = cartController;
		this.messageSource = messageSource;
		this.namedParameterService = namedParameterService;
		this.updateSamplePermission = updateSamplePermission;
		this.analysisSubmissionSampleProcessor = analysisSubmissionSampleProcessor;
		this.galaxyToolDataService = galaxyToolDataService;
		this.iridaPipelinePluginStyle = iridaPipelinePluginStyle;
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
		Set<AnalysisType> workflows = workflowsService.getDisplayableWorkflowTypes();

		List<Map<String, String>> flows = new ArrayList<>(workflows.size());
		workflows.stream().forEach(type -> {
			IridaWorkflow flow = null;
			try {
				flow = workflowsService.getDefaultWorkflowByType(type);
				IridaWorkflowDescription description = flow.getWorkflowDescription();
				String name = type.getType();
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
		model.addAttribute("pipeline_plugin_style", iridaPipelinePluginStyle);
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
		boolean canUpdateAllSamples;

		Map<Project, Set<Sample>> cartMap = cartController.getSelected();
		// Cannot run a pipeline on an empty cart!
		if (!cartMap.isEmpty()) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			IridaWorkflow flow = null;
			try {
				flow = workflowsService.getDisplayableIridaWorkflow(pipelineId);
			} catch (IridaWorkflowNotFoundException | IridaWorkflowNotDisplayableException e) {
				logger.error("Workflow not found or not displayable - See stack:", e);
				return "redirect:errors/not_found";
			}
			
			// Check if there even is functionality to update samples from results for this pipeline
			canUpdateAllSamples = analysisSubmissionSampleProcessor
					.hasRegisteredAnalysisSampleUpdater(flow.getWorkflowDescription().getAnalysisType());

			User user = userService.getUserByUsername(principal.getName());
			// Get all the reference files that could be used for this pipeline.
			List<Map<String, Object>> referenceFileList = new ArrayList<>();
			List<Map<String, Object>> projectList = new ArrayList<>();
			List<Map<String, Object>> addRefList = new ArrayList<>();
			IridaWorkflowDescription description = flow.getWorkflowDescription();
			final String workflowName = description.getName().toLowerCase();
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
						Collection<SampleSequencingObjectJoin> pairs = sequencingObjectService.getSequencesForSampleOfType(sample, SequenceFilePair.class);
						files.put("paired_end",
								pairs.stream().map(SampleSequencingObjectJoin::getObject).collect(Collectors.toList()));
					}

					// Singe end reads
					if (description.acceptsSingleSequenceFiles()) {
						Collection<SampleSequencingObjectJoin> singles = sequencingObjectService.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class);
						files.put("single_end",
								singles.stream().map(SampleSequencingObjectJoin::getObject)
										.collect(Collectors.toList()));
					}

					sampleMap.put("files", files);
					sampleList.add(sampleMap);
				}

				projectMap.put("id", project.getId().toString());
				projectMap.put("name", project.getLabel());
				projectMap.put("samples", sampleList);
				projectList.add(projectMap);
				
				canUpdateAllSamples &= updateSamplePermission.isAllowed(authentication, samples);
			}

			// Need to add the pipeline parameters
			final List<IridaWorkflowParameter> defaultWorkflowParameters = flow.getWorkflowDescription().getParameters();
			final List<Map<String, Object>> parameters = new ArrayList<>();
			if (defaultWorkflowParameters != null) {
				final List<Map<String, String>> defaultParameters = new ArrayList<>();
				for (IridaWorkflowParameter p : defaultWorkflowParameters) {
					if (p.isRequired()) {
						continue;
					}
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
				model.addAttribute("parameterModalTitle",
						messageSource.getMessage("pipeline.parameters.modal-title." + workflowName, null, locale));
			} else {
				model.addAttribute("noParameters", messageSource.getMessage("pipeline.no-parameters", null, locale));
			}
			// Parameters should be added not matter what, even if they are empty.
			model.addAttribute("parameters", parameters);

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
			model.addAttribute("canUpdateSamples", canUpdateAllSamples);
                        model.addAttribute("workflowName", workflowName);
			model.addAttribute("dynamicSourceRequired", description.requiresDynamicSource());
			model.addAttribute("analysisType", flow.getWorkflowDescription().getAnalysisType());

			final List<Map<String, Object>> dynamicSources = new ArrayList<>();
			if (description.requiresDynamicSource()) {
				TabularToolDataTable galaxyToolDataTable = new TabularToolDataTable();
				IridaWorkflowDynamicSourceGalaxy dynamicSource = new IridaWorkflowDynamicSourceGalaxy();
				for (IridaWorkflowParameter parameter : description.getParameters()) {
					if(parameter.isRequired() && parameter.hasDynamicSource()) {
						try {
							dynamicSource = parameter.getDynamicSource();
						} catch (IridaWorkflowParameterException e) {
							logger.debug("Dynamic Source error: ", e);
						}

						List<Object> parametersList = new ArrayList<>();
						String dynamicSourceName;
						Map<String, Object> toolDataTable = new HashMap<>();
						try {
							dynamicSourceName = dynamicSource.getName();
							toolDataTable.put("id", dynamicSourceName);
							toolDataTable.put("label", messageSource.getMessage("dynamicsource.label." + dynamicSourceName, null, locale));
							toolDataTable.put("parameters", parametersList);

							galaxyToolDataTable = galaxyToolDataService.getToolDataTable(dynamicSourceName);
							List<String> labels = galaxyToolDataTable.getFieldsForColumn(dynamicSource.getDisplayColumn());
							Iterator<String> labelsIterator = labels.iterator();
							List<String> values = galaxyToolDataTable.getFieldsForColumn(dynamicSource.getParameterColumn());
							Iterator<String> valuesIterator = values.iterator();

							while (labelsIterator.hasNext() && valuesIterator.hasNext()) {
								String label = labelsIterator.next();
								String value = valuesIterator.next();
								HashMap<String, String> toolDataTableFieldsMap = new HashMap<>();
								toolDataTableFieldsMap.put("label", label);
								toolDataTableFieldsMap.put("value", value);
								toolDataTableFieldsMap.put("name", parameter.getName());
								parametersList.add(toolDataTableFieldsMap);
							}
							dynamicSources.add(toolDataTable);
						} catch (Exception e) {
							logger.debug("Tool Data Table not found: ", e);
						}
					}
				}
				model.addAttribute("dynamicSources", dynamicSources);
			}

			final List<Map<String, Object>> paramsWithChoices = description.getParameters()
					.stream()
					.filter(IridaWorkflowParameter::hasChoices)
					.map(x -> ImmutableMap.of("label", localizedParamLabel(locale, workflowName, x.getName()), "name",
							x.getName(), "choices", x.getChoices()
									.stream()
									.map(c -> ImmutableMap.of("name", localizedParamOptionLabel(locale, workflowName, x.getName(), c.getName()), "value", c.getValue()))
									.collect(Collectors.toList())))
					.collect(Collectors.toList());
			model.addAttribute("paramsWithChoices", paramsWithChoices);
			response = URL_GENERIC_PIPELINE;
		}

		return response;
	}

	/**
	 * Get localized workflow parameter label.
	 *
	 * If the localized workflow parameter label text is not found by the {@link MessageSource}, then log the
	 * NoSuchMessageException and return the `paramName` as the localized parameter label.
	 *
	 * @param locale Message locale
	 * @param workflowName Workflow name
	 * @param paramName Parameter name
	 * @return Localized parameter label if found in {@link MessageSource}; otherwise, return `paramName`.
	 */
	private String localizedParamLabel(Locale locale, String workflowName, String paramName) {
		final String messageName = "pipeline.parameters." + workflowName + "." + paramName;
		try {
			return messageSource.getMessage(messageName, null, locale);
		} catch (NoSuchMessageException e) {
			logger.error("Couldn't find message for '" + messageName + "': ", e);
			return paramName;
		}
	}

	private String localizedParamOptionLabel(Locale locale, String workflowName, String paramName, String optionName) {
		final String messageName = "pipeline.parameters." + workflowName + "." + paramName + "." + optionName;
		try {
			return messageSource.getMessage(messageName, null, locale);
		} catch (NoSuchMessageException e) {
			logger.error("Couldn't find message for '" + messageName + "': ", e);
			return paramName + "." + optionName;
		}
	}


	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	/**
	 * Launch a pipeline
	 *
	 * @param locale     the locale that the browser is using for the current request.
	 * @param parameters DTO of pipeline start parameters
	 * @return a JSON response with the status and any messages.
	 */
	@RequestMapping(value = "/ajax/start", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> ajaxStartPipeline(Locale locale, @RequestBody final PipelineStartParameters parameters) {
		try {
			IridaWorkflow flow = workflowsService.getDisplayableIridaWorkflow(parameters.getWorkflowId());
			IridaWorkflowDescription description = flow.getWorkflowDescription();

			// The pipeline needs to have a name.
			String name = parameters.getName();
			if (Strings.isNullOrEmpty(name)) {
				return ImmutableMap.of("error", messageSource.getMessage("workflow.no-name-provided", null, locale));
			}


			// Check to see if a reference file is required.
			Long ref = parameters.getRef();
			if (description.requiresReference() && ref == null) {
				return ImmutableMap.of("error",
						messageSource.getMessage("pipeline.error.no-reference.pipeline-start", null, locale));
			}

			// Get a list of the files to submit
			List<SingleEndSequenceFile> singleEndFiles = new ArrayList<>();
			List<SequenceFilePair> sequenceFilePairs = new ArrayList<>();
			List<Long> single = parameters.getSingle();
			if (single != null) {
				Iterable<SequencingObject> readMultiple = sequencingObjectService.readMultiple(single);
				
				readMultiple.forEach(f -> {
					if (!(f instanceof SingleEndSequenceFile)) {
						throw new IllegalArgumentException("file " + f.getId() + " not a SingleEndSequenceFile");
					}
					
					singleEndFiles.add((SingleEndSequenceFile) f);
				});
				
				// Check the single files for duplicates in a sample, throws SampleAnalysisDuplicateException
				sequencingObjectService.getUniqueSamplesForSequencingObjects(Sets.newHashSet(singleEndFiles));
			}
			List<Long> paired = parameters.getPaired();
			if (paired != null) {
				Iterable<SequencingObject> readMultiple = sequencingObjectService.readMultiple(paired);
				
				readMultiple.forEach(f -> {
					if (!(f instanceof SequenceFilePair)) {
						throw new IllegalArgumentException("file " + f.getId() + " not a SequenceFilePair");
					}
					
					sequenceFilePairs.add((SequenceFilePair) f);
				});
				
				// Check the pair files for duplicates in a sample, throws SampleAnalysisDuplicateException
				sequencingObjectService.getUniqueSamplesForSequencingObjects(Sets.newHashSet(sequenceFilePairs));
			}

			// Get the pipeline parameters
			Map<String, String> params = new HashMap<>();
			IridaWorkflowNamedParameters namedParameters = null;
			Map<String, Object> selectedParameters = parameters.getSelectedParameters();
			if (selectedParameters != null) {
				try {
					final String selectedParametersId = selectedParameters.get("id")
							.toString();
					if (!DEFAULT_WORKFLOW_PARAMETERS_ID.equals(selectedParametersId)
							&& !CUSTOM_UNSAVED_WORKFLOW_PARAMETERS_ID.equals(selectedParametersId)) {
						// this means that a named parameter set was selected
						// and unmodified, so load up that named parameter set
						// to pass along.
						namedParameters = namedParameterService.read(Long.valueOf(selectedParametersId));
					} else {
						@SuppressWarnings("unchecked") final List<Map<String, String>> unnamedParameters = (List<Map<String, String>>) selectedParameters.get(
								"parameters");
						for (final Map<String, String> parameter : unnamedParameters) {
							params.put(parameter.get("name"), parameter.get("value"));
						}
					}
				} catch (Exception e) {
					return ImmutableMap.of("parameterError",
							messageSource.getMessage("pipeline.parameters.error", null, locale));
				}
			}

			List<Project> projectsToShare = new ArrayList<>();
			List<Long> sharedProjects = parameters.getSharedProjects();
			if (sharedProjects != null && !sharedProjects.isEmpty()) {
				projectsToShare = Lists.newArrayList(projectService.readMultiple(sharedProjects));
			}

			String analysisDescription = parameters.getDescription();
			Boolean writeResultsToSamples = parameters.getWriteResultsToSamples();
			if (description.getInputs()
					.requiresSingleSample()) {
				analysisSubmissionService.createSingleSampleSubmission(flow, ref, singleEndFiles, sequenceFilePairs,
						params, namedParameters, name, analysisDescription, projectsToShare, writeResultsToSamples);
			} else {
				analysisSubmissionService.createMultipleSampleSubmission(flow, ref, singleEndFiles, sequenceFilePairs,
						params, namedParameters, name, analysisDescription, projectsToShare, writeResultsToSamples);
			}

		} catch (IridaWorkflowNotFoundException | IridaWorkflowNotDisplayableException e) {
			logger.error("Cannot find or cannot launch IridaWorkflow [" + parameters.getWorkflowId() + "]", e);
			return ImmutableMap.of("pipelineError",
					messageSource.getMessage("pipeline.error.invalid-pipeline", null, locale));
		} catch (DuplicateSampleException e) {
			logger.error("Multiple files for Sample found", e);
			return ImmutableMap.of("pipelineError",
					messageSource.getMessage("pipeline.error.duplicate-samples", null, locale));
		}

		return ImmutableMap.of("success", true);
	}

	/**
	 * Get {@link IridaWorkflowDescription} for a workflow/pipeline UUID.
	 * @param pipelineUUID Workflow/Pipeline UUID
	 * @return Map corresponding to a {@link IridaWorkflowDescription}.
	 * @throws IridaWorkflowNotFoundException if workflow could not be found.
	 */
	@RequestMapping(value = "/ajax/{pipelineUUID}")
	@ResponseBody
	public IridaWorkflowDescription getPipelineInfo(@PathVariable UUID pipelineUUID) throws IridaWorkflowNotFoundException {
		return workflowsService.getIridaWorkflowOrUnknown(pipelineUUID).getWorkflowDescription();
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

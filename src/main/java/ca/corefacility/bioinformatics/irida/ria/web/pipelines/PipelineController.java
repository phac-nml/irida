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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileWebUtilities;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Controller for pipeline related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
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
			CartController cartController,
			MessageSource messageSource) {
		this.sequenceFileService = sequenceFileService;
		this.sequenceFilePairService = sequenceFilePairService;
		this.referenceFileService = referenceFileService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.projectService = projectService;
		this.userService = userService;
		this.cartController = cartController;
		this.messageSource = messageSource;
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
								.getMessage(key + ".title", new Object[]{}, locale),
						"description",
						messageSource
								.getMessage(key + ".description", new Object[]{}, locale)
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

	@RequestMapping(value = "/{pipelineId}")
	public String getPhylogenomicsPage(final Model model, Principal principal, Locale locale, @PathVariable UUID pipelineId) {
		String response = URL_EMPTY_CART_REDIRECT;

		Map<Project, Set<Sample>> cartMap = cartController.getSelected();
		// Cannot run a pipeline on an empty cart!
		if (!cartMap.isEmpty()) {

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
			SequenceFileWebUtilities sequenceFileWebUtilities = new SequenceFileWebUtilities();
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
					List<Map<String, Object>> fileList = new ArrayList<>();

					// Paired end reads
					if (description.acceptsPairedSequenceFiles()) {
						List<SequenceFilePair> sequenceFilePairs = sequenceFilePairService
								.getSequenceFilePairsForSample(sample);
						for (SequenceFilePair pair : sequenceFilePairs) {
							List<Map<String, Object>> fileMap = pair.getFiles().stream()
									.map(sequenceFileWebUtilities::getFileDataMap).collect(Collectors.toList());
							fileList.add(ImmutableMap.of(
									"id", pair.getId(),
									"type", "paired_end",
									"files", fileMap,
									"createdDate", pair.getCreatedDate()
							));
						}
					}

					// Singe end reads
					if (description.acceptsSingleSequenceFiles()) {
						List<Join<Sample, SequenceFile>> sfJoin = sequenceFileService.getSequenceFilesForSample(sample);
						for (Join<Sample, SequenceFile> join : sfJoin) {
							Map<String, Object> fileMap = sequenceFileWebUtilities.getFileDataMap(join.getObject());
							fileMap.put("type", "single_end");
							fileList.add(fileMap);
						}
					}

					sampleMap.put("files", fileList);
					sampleList.add(sampleMap);
				}

				projectMap.put("id", project.getId().toString());
				projectMap.put("name", project.getLabel());
				projectMap.put("samples", sampleList);
				projectList.add(projectMap);
			}

			// Need to add the pipeline parameters
			List<IridaWorkflowParameter> paras = flow.getWorkflowDescription().getParameters();
			List<Map<String, String>> parameters = new ArrayList<>();
			if (paras != null) {
				String name = description.getName().toLowerCase();
				for (IridaWorkflowParameter p : paras) {
					parameters.add(ImmutableMap.of(
							"label",
							messageSource.getMessage("pipeline.parameters." + name + "." + p.getName(), null, locale),
							"value", p.getDefaultValue(),
							"name", p.getName()
					));
				}
				model.addAttribute("parameters", parameters);
				model.addAttribute("parameterModalTitle",
						messageSource.getMessage("pipeline.parameters.modal-title." + name, null, locale));
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
	 * @param pipelineId
	 * 		the id for the {@link IridaWorkflow}
	 * @param single
	 * 		a list of {@link SequenceFile} id's
	 * @param paired
	 * 		a list of {@link SequenceFilePair} id's
	 * 	@param parameters
	 * 	    TODO: This is a hack! Update when fixing issue #100
	 *      {@link Map} of ALL parameters passed.  Only want the 'paras' object --> a {@link Map} of pipeline parameters
	 * @param ref
	 * 		the id for a {@link ReferenceFile}
	 * @param name
	 * 		a user provided name for the {@link IridaWorkflow}
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
			Map<String, String> params = null;
			if (parameters.containsKey("paras")) {
				params = extractPipelineParameters(parameters.get("paras"));
				if (params.containsKey("parameterError")) {
					return ImmutableMap
							.of("parameterError", messageSource.getMessage("pipeline.parameters.error", null, locale));
				}
			}

			// TODO [15-02-17] (Josh): Replace this once the description has a setting for multiple files.
			String type = description.getAnalysisType().toString();
			if (type.equals("phylogenomics")) {
				submitMultipleFileWorkflow(flow, ref, sequenceFiles, sequenceFilePairs, params, name);

			} else {
				submitSingleSampleWorkflow(flow, ref, sequenceFiles, sequenceFilePairs, params, name);
			}

		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Cannot file IridaWorkflow [" + pipelineId + "]", e);
			result = ImmutableMap
					.of("pipelineError", messageSource.getMessage("pipeline.error.invalid-pipeline", null, locale));
		} catch (DuplicateSampleException e) {
			logger.error("Multiple files for Sample found", e);
		}

		return result;
	}

	/**
	 * Submit {@link AnalysisSubmission} for workflows requiring only one {@link SequenceFile} or {@link
	 * SequenceFilePair}
	 *
	 * @param workflow {@link IridaWorkflow} that the files will be run on
	 * @param ref {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs {@link List} of {@link SequenceFilePair} to run on the workflow
	 * @param params {@link Map} of parameters specific for the pipeline
	 * @param name {@link String} the name for the analysis
	 */
	private void submitSingleSampleWorkflow(IridaWorkflow workflow, Long ref, List<SequenceFile> sequenceFiles,
			List<SequenceFilePair> sequenceFilePairs, Map<String, String> params, String name) {
		// Single end reads
		IridaWorkflowDescription description = workflow.getWorkflowDescription();
		int count = 0;
		if (description.acceptsSingleSequenceFiles()) {
			for (SequenceFile file : sequenceFiles) {
				// Build the analysis submission
				AnalysisSubmission.Builder builder = AnalysisSubmission.builder(workflow.getWorkflowIdentifier());
				builder.name(name + "_" + ++count);
				builder.inputFilesSingle(ImmutableSet.of(file));

				// Add reference file
				if (ref != null && description.requiresReference()) {
					// Note: This cannot be empty if through the UI if the pipeline required a reference file.
					ReferenceFile referenceFile = referenceFileService.read(ref);
					builder.referenceFile(referenceFile);
				}

				if (params != null && description.acceptsParameters()) {
					// Note: This cannot be empty if through the UI if the pipeline required params.
					builder.inputParameters(params);
				}

				// Create the submission
				analysisSubmissionService.create(builder.build());
			}
		}

		// Paired end reads
		if (description.acceptsPairedSequenceFiles()) {
			for (SequenceFilePair pair : sequenceFilePairs) {
				// Build the analysis submission
				AnalysisSubmission.Builder builder = AnalysisSubmission.builder(workflow.getWorkflowIdentifier());
				builder.name(name + "_" + ++count);
				builder.inputFilesPaired(ImmutableSet.of(pair));

				// Add reference file
				if (ref != null && description.requiresReference()) {
					ReferenceFile referenceFile = referenceFileService.read(ref);
					builder.referenceFile(referenceFile);
				}

				if (description.acceptsParameters()) {
					builder.inputParameters(params);
				}

				// Create the submission
				analysisSubmissionService.create(builder.build());
			}
		}
	}

	/**
	 * Submit {@link AnalysisSubmission} for workflows allowing multiple one {@link SequenceFile} or {@link
	 * SequenceFilePair}
	 *
	 * @param workflow {@link IridaWorkflow} that the files will be run on
	 * @param ref {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs {@link List} of {@link SequenceFilePair} to run on the workflow
	 * @param params {@link Map} of parameters specific for the pipeline
	 * @param name {@link String} the name for the analysis
	 */
	private void submitMultipleFileWorkflow(IridaWorkflow workflow, Long ref, List<SequenceFile> sequenceFiles,
			List<SequenceFilePair> sequenceFilePairs, Map<String, String> params, String name) {
		AnalysisSubmission.Builder builder = AnalysisSubmission.builder(workflow.getWorkflowIdentifier());
		builder.name(name);
		IridaWorkflowDescription description = workflow.getWorkflowDescription();

		// Add reference file
		if (ref != null && description.requiresReference()) {
			ReferenceFile referenceFile = referenceFileService.read(ref);
			builder.referenceFile(referenceFile);
		}

		// Add any single end sequencing files.
		if (!sequenceFiles.isEmpty() && description.acceptsSingleSequenceFiles()) {
			builder.inputFilesSingle(Sets.newHashSet(sequenceFiles));
		}

		// Add any paired end sequencing files.
		if (!sequenceFilePairs.isEmpty() && description.acceptsPairedSequenceFiles()) {
			builder.inputFilesPaired(Sets.newHashSet(sequenceFilePairs));
		}

		if (description.acceptsParameters()) {
			builder.inputParameters(params);
		}

		// Create the submission
		analysisSubmissionService.create(builder.build());
	}

	/**
	 * Extract {@link IridaWorkflow} parameters from the request {@link Map}
	 *
	 * @param mapString
	 * 		{@link Map} of parameters
	 *
	 * @return {@link Map} of parameters for the pipeline
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> extractPipelineParameters(String mapString) {
		// TODO [15-02-16] (Josh): Update when addressing issue #100
		Map<String, String> result;
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.readValue(mapString, Map.class);
		} catch (IOException e) {
			logger.error("Error extracting parameters from submission", e);
			result = ImmutableMap
					.of("parameterError", "");
		}
		return result;
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

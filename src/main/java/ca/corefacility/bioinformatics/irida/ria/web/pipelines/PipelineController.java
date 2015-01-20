package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
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
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.components.PipelineSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Controller for pipeline related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@Scope("session")
@RequestMapping(PipelineController.BASE_URL)
public class PipelineController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);
	/*
	 * CONSTANTS
	 */

	// URI's
	public static final String BASE_URL = "/pipelines";
	public static final String URL_EMPTY_CART_REDIRECT = "redirect:/pipelines";
	public static final String URL_LAUNCH ="pipelines/pipeline_selection";
	public static final String URL_PHYLOGENOMICS = "pipelines/types/phylogenomics";

	public static final String URI_LIST_PIPELINES = "/ajax/list.json";
	public static final String URI_AJAX_START_PIPELINE = "/ajax/start.json";
	public static final String URI_AJAX_CART_LIST = "/ajax/cart_list.json";

	// JSON KEYS
	public static final String JSON_KEY_SAMPLE_ID = "id";
	public static final String JSON_KEY_SAMPLE_OMIT_FILES_LIST = "omit";

	/*
	 * SERVICES
	 */
	private SampleService sampleService;
	private ReferenceFileService referenceFileService;
	private SequenceFileService sequenceFileService;
	private AnalysisSubmissionService analysisSubmissionService;
	private ProjectService projectService;
	private UserService userService;
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;

	/*
	 * CONTROLLERS
	 */
	private CartController cartController;

	/*
	 * COMPONENTS
	 */
	private PipelineSubmission pipelineSubmission;

	@Autowired
	public PipelineController(SampleService sampleService, SequenceFileService sequenceFileService,
			ReferenceFileService referenceFileService,
			AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService,
			ProjectService projectService,
			UserService userService,
			CartController cartController,
			MessageSource messageSource) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		this.referenceFileService = referenceFileService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.projectService = projectService;
		this.userService = userService;
		this.cartController = cartController;
		this.messageSource = messageSource;

		this.pipelineSubmission = new PipelineSubmission();
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
			String name = type.toString();
			String key = "workflow." + name;
			flows.add(ImmutableMap.of(
					"name", name,
					"title",
					messageSource
							.getMessage(key + ".title", new Object[] { }, locale),
					"description",
					messageSource
							.getMessage(key + ".description", new Object[] { }, locale)
			));
		});
		model.addAttribute("counts", getCartSummaryMap());
		model.addAttribute("workflows", flows);
		return URL_LAUNCH;
	}

	@RequestMapping(value = "/phylogenomics")
	public String getPhylogenomicsPage(final Model model, Principal principal) {
		String response = URL_EMPTY_CART_REDIRECT;

		Map<Project, Set<Sample>> cartMap = cartController.getSelected();
		// Cannot run a pipeline on an empty cart!
		if (!cartMap.isEmpty()) {
			User user = userService.getUserByUsername(principal.getName());
			// Get all the reference files that could be used for this pipeline.
			List<Map<String, Object>> referenceFileList = new ArrayList<>();
			List<Map<String, Object>> fileList = new ArrayList<>();
			List<Map<String, Object>> addRefList = new ArrayList<>();
			for (Project project : cartMap.keySet()) {
				List<Join<Project, ReferenceFile>> joinList = referenceFileService.getReferenceFilesForProject(project);
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

				Set<Sample> samples = cartMap.get(project);
				Map<String, Object> projectMap = new HashMap<>();
				List<Map<String, Object>> sampleList = new ArrayList<>();
				for (Sample sample : samples) {
					Map<String, Object> sampleMap = new HashMap<>();
					sampleMap.put("name", sample.getLabel());
					sampleMap.put("id", sample.getId().toString());

					// Singe end reads
					List<Join<Sample, SequenceFile>> sfJoin = sequenceFileService.getSequenceFilesForSample(sample);
					sampleMap.put("singles", sfJoin.stream().map(Join::getObject)
							.collect(Collectors.toList()));

					// Paired end reads
					List<SequenceFilePair> sequenceFilePairs = sequenceFileService
							.getSequenceFilePairsForSample(sample);
					sampleMap.put("pairs", sequenceFilePairs);

					sampleList.add(sampleMap);
				}

				projectMap.put("id", project.getId().toString());
				projectMap.put("name", project.getLabel());
				projectMap.put("samples", sampleList);
				fileList.add(projectMap);
			}
			model.addAttribute("referenceFiles", referenceFileList);
			model.addAttribute("addRefProjects", addRefList);
			model.addAttribute("files", fileList);
			response = URL_PHYLOGENOMICS;
		}

		return response;
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	/**
	 * Get a list of pipelines that can be run on the dataset provided
	 * @return  A list of pipeline types and names that can be run on the provided dataset.
	 */
	@RequestMapping(value = URI_LIST_PIPELINES, method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, String>> ajaxCreateNewPipelineFromProject(
			@RequestBody List<Map<String, Object>> json) {

		// Since the UI only knows about sample id's (unless the files view is expanded) only a list
		// of sample id's are passed to the server.  If the user opens the sample files view, they can
		// deselect specific files.  These are added to an omit files list.
		ArrayList<Long> fileIds = new ArrayList<>();
		for (Map<String, Object> map : json) {
			Long id = Long.parseLong((String) map.get(JSON_KEY_SAMPLE_ID));
			@SuppressWarnings("unchecked")
			Set<String> omit = ImmutableSet.copyOf((List<String>) map.get(JSON_KEY_SAMPLE_OMIT_FILES_LIST));

			Sample sample = sampleService.read(id);
			List<Join<Sample, SequenceFile>> fileList = sequenceFileService.getSequenceFilesForSample(sample);
			for(Join<Sample, SequenceFile> join : fileList) {
				Long fileId = join.getObject().getId();
				if (!omit.contains(fileId.toString())) {
					fileIds.add(fileId);
				}
			}
		}
		// TODO: (14-08-28 - Josh) Need to determine what pipelines can be run with these files.
		Iterable<SequenceFile> files = sequenceFileService.readMultiple(fileIds);
		pipelineSubmission.setSequenceFiles(files);

		// TODO: (14-08-13 - Josh) Get real data from Aaron's stuff
		List<Map<String, String>> response = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("id", "1");
		map.put("text", "Whole Genome Phylogenomics Pipeline");
		response.add(map);
		return response;
	}

	/**
	 * Start a new pipeline based on the pipeline id
	 *
	 * @param pId      Id for the type of pipeline
	 * @param rId      Id for the reference file
	 * @param response {@link HttpServletResponse}
	 * @return  A response defining the status of the pipeline submission (success or failure).
	 */
	@RequestMapping(value = URI_AJAX_START_PIPELINE, produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> ajaxStartNewPipelines(@RequestParam Long pId,
			@RequestParam Long rId, @RequestParam String name, HttpServletResponse response) {
		pipelineSubmission.setReferenceFile(referenceFileService.read(rId));
		if (Strings.isNullOrEmpty(name)) {
			// TODO: (14-09-02 - Josh) This needs be be found from the repository based on the ID.
			name = "Whole Genome Phylogenomcis Pipeline";
		}
		List<Map<String, String>> result = new ArrayList<>();
		try {
			startPipeline(pId, name);
			result.add(ImmutableMap.of("success", "success"));
		} catch (EntityExistsException | ConstraintViolationException e) {
			logger.error("Error submitting pipeline (id = " + pId + ") [" + e.getMessage() + "]");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result.add(ImmutableMap.of("error", messageSource.getMessage("pipelines.start.failure", null,
					LocaleContextHolder.getLocale())));
		}
		return result;
	}

	// ************************************************************************************************
	// RUNNING PIPELINE INTERNAL METHODS
	// ************************************************************************************************

	private void startPipeline(Long pipelineId, String name) {
		// TODO: (14-08-28 - Josh) pipelineId needs to be passed b/c front end does not need to know the details.
		AnalysisSubmission asp = AnalysisSubmission.createSubmissionSingleReference(name, pipelineSubmission.getSequenceFiles(),
				pipelineSubmission.getReferenceFile(),
				UUID.randomUUID());
		
		AnalysisSubmission createdSubmission = analysisSubmissionService.create(asp);
		logger.debug("Successfully submitted analysis: " + createdSubmission);

		// Reset the pipeline submission
		pipelineSubmission.clear();
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

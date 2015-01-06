package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.components.PipelineSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

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
@RequestMapping(value = "/pipelines")
public class PipelineController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);
	/*
	 * CONSTANTS
	 */

	// URI's
	public static final String URI_LIST_PIPELINES = "/ajax/list.json";
	public static final String URI_AJAX_START_PIPELINE = "/ajax/start.json";

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
	private IridaWorkflowsService workflowsService;
	private MessageSource messageSource;

	/*
	 * COMPONENTS
	 */
	private PipelineSubmission pipelineSubmission;

	@Autowired
	public PipelineController(SampleService sampleService, SequenceFileService sequenceFileService,
			ReferenceFileService referenceFileService,
			AnalysisSubmissionService analysisSubmissionService,
			IridaWorkflowsService iridaWorkflowsService,
			MessageSource messageSource) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		this.referenceFileService = referenceFileService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = iridaWorkflowsService;
		this.messageSource = messageSource;

		this.pipelineSubmission = new PipelineSubmission();
	}

	@RequestMapping
	public String getPipelineLaunchPage(final Model model, Locale locale) {
		Set<AnalysisType> workflows = Sets.newHashSet(AnalysisType.values());
		workflows.remove(AnalysisType.DEFAULT);
		//workflowsService.getRegisteredWorkflowTypes();

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
		model.addAttribute("workflows", flows);
		return "pipelines/pipeline_selection";
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
		AnalysisSubmission asp = new AnalysisSubmission(name, pipelineSubmission.getSequenceFiles(),
				pipelineSubmission.getReferenceFile(),
				UUID.randomUUID());
		
		AnalysisSubmission createdSubmission = analysisSubmissionService.create(asp);
		logger.debug("Successfully submitted analysis: " + createdSubmission);

		// Reset the pipeline submission
		pipelineSubmission.clear();
	}
}

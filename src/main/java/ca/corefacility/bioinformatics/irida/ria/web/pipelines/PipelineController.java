package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.ria.components.PipelineSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

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
	public static final String JSON_KEY_SAMPLE_ID = "id";
	public static final String JSON_KEY_SAMPLE_OMIT_FILES_LIST = "omit";

	/*
	 * SERVICES
	 */
	private SampleService sampleService;
	private ReferenceFileService referenceFileService;
	private SequenceFileService sequenceFileService;
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics;
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	/*
	 * COMPONENTS
	 */
	private PipelineSubmission pipelineSubmission;

	@Autowired
	public PipelineController(SampleService sampleService, SequenceFileService sequenceFileService,
			ReferenceFileService referenceFileService,
			RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics,
			AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		this.referenceFileService = referenceFileService;
		this.remoteWorkflowServicePhylogenomics = remoteWorkflowServicePhylogenomics;
		this.analysisExecutionServicePhylogenomics = analysisExecutionServicePhylogenomics;

		this.pipelineSubmission = new PipelineSubmission();
	}

	// ************************************************************************************************
	// AJAX
	// ************************************************************************************************

	/**
	 * Get a list of pipelines that can be run on the dataset provided
	 * @return
	 */
	@RequestMapping(value = URI_LIST_PIPELINES, method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, String>> ajaxCreateNewPipelineFromProject(
			@RequestBody List<Map<String, Object>> json) {

		// Since the UI only knows about sample id's (unless the files view is expanded) only a list
		// of sample id's are passed to the server.  If the user opens the sample files view, they can
		// deselect specific files.  These are added to an omit files list.
		ArrayList<Long> fileIds = new ArrayList<>();
		for (Map map : json) {
			Long id = Long.parseLong((String) map.get(JSON_KEY_SAMPLE_ID));
			Set omit = ImmutableSet.copyOf((List) map.get(JSON_KEY_SAMPLE_OMIT_FILES_LIST));

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
	 * @param pId      Id for the pipeline
	 * @param rId      Id for the reference file
	 * @param response {@link HttpServletResponse}
	 * @return
	 */
	@RequestMapping(value = URI_AJAX_START_PIPELINE, produces = MediaType.APPLICATION_JSON_VALUE,
			method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> ajaxStartNewPipelines(@RequestParam Long pId,
			@RequestParam Long rId, HttpServletResponse response) {
		pipelineSubmission.setReferenceFile(referenceFileService.read(rId));
		List<Map<String, String>> result = new ArrayList<>();
		try {
			startPipeline(pId);
			result.add(ImmutableMap.of("success", "success"));
		} catch (ExecutionManagerException e) {
			logger.error("Error starting pipeline (id = " + pId + ") [" + e.getMessage() + "]");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result.add(ImmutableMap.of("error", e.getMessage()));
		}
		return result;
	}

	// ************************************************************************************************
	// RUNNING PIPELINE INTERNAL METHODS
	// ************************************************************************************************

	private void startPipeline(Long pipelineId) throws ExecutionManagerException {
		// TODO: (14-08-28 - Josh) pipelineId needs to be passed b/c front end does not need to know the details.
		RemoteWorkflowPhylogenomics workflow = remoteWorkflowServicePhylogenomics.getCurrentWorkflow();
		AnalysisSubmissionPhylogenomics asp = new AnalysisSubmissionPhylogenomics(pipelineSubmission.getSequenceFiles(),
				pipelineSubmission.getReferenceFile(),
				workflow);
		analysisExecutionServicePhylogenomics.executeAnalysis(asp);

		// Reset the pipeline submission
		pipelineSubmission.clear();
	}
}

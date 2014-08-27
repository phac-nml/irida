package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.components.PipelineSubmission;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

/**
 * Controller for pipeline related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@Scope("session")
@RequestMapping(value = "/pipelines")
public class PipelineController {
	private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);
	/*
	 * CONSTANTS
	 */

	// Directories
	public static final String PIPELINE_DIR = "pipelines/";

	// URI's
	public static final String URI_LIST_PIPELINES = "/ajax/list";

	/*
	 * SERVICES
	 */
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics;
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	/*
	 * COMPONENTS
	 */
	private PipelineSubmission pipelineSubmission;

	@Autowired
	public PipelineController(SampleService sampleService, SequenceFileService sequenceFileService,
			RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics,
			AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics,
			PipelineSubmission pipelineSubmission) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		this.remoteWorkflowServicePhylogenomics = remoteWorkflowServicePhylogenomics;
		this.analysisExecutionServicePhylogenomics = analysisExecutionServicePhylogenomics;
		this.pipelineSubmission = pipelineSubmission;
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
		ArrayList<Long> fileIds = new ArrayList<>();
		for (Map map : json) {
			Long id = Long.parseLong((String) map.get("id"));
			List omit = (List) map.get("omit");

			Sample sample = sampleService.read(id);
			List<Join<Sample, SequenceFile>> fileList = sequenceFileService.getSequenceFilesForSample(sample);
			for(Join<Sample, SequenceFile> join : fileList) {
				Long fileId = join.getObject().getId();
				if (!omit.contains(fileId.toString())) {
					fileIds.add(fileId);
				}
			}
		}
		// TODO: (14-08-28 - Josh) Need to determine what reference files and pipelines can be run with these files.
		pipelineSubmission.setSequenceFiles(fileIds);

		// TODO: (14-08-13 - Josh) Get real data from Aaron's stuff
		List<Map<String, String>> response = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("id", "1");
		map.put("text", "Whole Genome Phylogenomics Pipeline");
		response.add(map);
		return response;
	}

	@RequestMapping(value = "/ajax/start.json", produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, String>> ajaxStartNewPipelines(@RequestParam(value = "pId") Long pipelineId) {

		return null;
	}
}

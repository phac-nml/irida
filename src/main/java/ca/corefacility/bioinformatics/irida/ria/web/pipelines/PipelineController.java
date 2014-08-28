package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ca.corefacility.bioinformatics.irida.ria.components.PipelineSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableMap;

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

	/*
	 * SERVICES
	 */
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;

	/*
	 * COMPONENTS
	 */
	private PipelineSubmission pipelineSubmission;

	@Autowired
	public PipelineController(SampleService sampleService, SequenceFileService sequenceFileService,
			PipelineSubmission pipelineSubmission) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
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
		// TODO: (14-08-28 - Josh) Need to determine what pipelines can be run with these files.
		pipelineSubmission.setSequenceFiles(fileIds);

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
		pipelineSubmission.setReferenceFile(rId);
		List<Map<String, String>> result = new ArrayList<>();
		try {
			pipelineSubmission.startPipeline(pId);
			result.add(ImmutableMap.of("success", "success"));
		} catch (ExecutionManagerException e) {
			logger.error("Error starting pipeline (id = " + pId + ") [" + e.getMessage() + "]");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result.add(ImmutableMap.of("error", e.getMessage()));
		}
		return result;
	}
}

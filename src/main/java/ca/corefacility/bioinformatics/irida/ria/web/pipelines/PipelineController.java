package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Controller for pipeline related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/pipelines")
public class PipelineController {
	public static final String PIPELINE_DIR = "pipelines/";
	private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);

	/*
	 * SERVICES
	 */
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;

	@Autowired
	public PipelineController(SampleService sampleService, SequenceFileService sequenceFileService) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
	}

	/**
	 * Get a list of pipelines that can be run on the dataset provided
	 * @return
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
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

		// TODO: (14-08-13 - Josh) Get real data from Aaron's stuff
		List<Map<String, String>> response = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("id", "1");
		map.put("text", "Whole Genome Phylogenomics Pipeline");
		response.add(map);
		return response;
	}
}

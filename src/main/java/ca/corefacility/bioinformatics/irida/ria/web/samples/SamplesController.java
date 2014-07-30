package ca.corefacility.bioinformatics.irida.ria.web.samples;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.utilities.Formats;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for all sample related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/samples")
public class SamplesController {
	// Page Names
	private static final String SAMPlES_DIR = "samples/";
	private static final String SAMPLE_PAGE = SAMPlES_DIR + "sample";

	// Services
	private final SampleService sampleService;
	private final SequenceFileService sequenceFileService;

	@Autowired
	public SamplesController(SampleService sampleService, SequenceFileService sequenceFileService) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
	}

	/**
	 * Get a list of details about files associated with a specific sample
	 *
	 * @param sampleId The id of the sample to find the files for.
	 * @return A list file details.
	 */
	@RequestMapping(value = "/ajax/{sampleId}/files", produces = MediaType.APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	List<Map<String, Object>> getFilesForSample(@PathVariable Long sampleId) {
		Sample sample = sampleService.read(sampleId);
		List<Join<Sample, SequenceFile>> joinList = sequenceFileService.getSequenceFilesForSample(sample);

		List<Map<String, Object>> response = new ArrayList<>();
		for (Join<Sample, SequenceFile> join : joinList) {
			SequenceFile file = join.getObject();
			Map<String, Object> map = new HashMap<>();
			map.put("id", file.getId().toString());
			map.put("name", file.getLabel());
			map.put("created", Formats.DATE.format(file.getTimestamp()));
			response.add(map);
		}
		return response;
	}
}

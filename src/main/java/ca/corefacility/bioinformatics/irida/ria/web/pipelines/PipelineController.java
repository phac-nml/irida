package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

	public PipelineController() {
	}

	/**
	 * Get a list of pipelines that can be run on the dataset provided
	 * @return
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, String>> ajaxCreateNewPipelineFromProject(@RequestParam List<Map<String, Object>> files) {
		List<Map<String, String>> response = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("id", "1");
		map.put("text", "Aaron's Awesome Pipeline");
		response.add(map);
		return response;
	}
}

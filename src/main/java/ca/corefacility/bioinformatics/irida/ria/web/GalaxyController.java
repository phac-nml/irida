package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;

/**
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/galaxy")
public class GalaxyController {
	private static final Logger logger = LoggerFactory.getLogger(GalaxyController.class);

	@RequestMapping("/poll/{sessionId}")
	public @ResponseBody Map<String, Object> pollGalaxy(@PathVariable String sessionId, HttpServletRequest request) {
		UploadWorker worker = (UploadWorker) request.getSession().getAttribute(sessionId);

		Map<String, Object> result = new HashMap<>();

		result.put("progress", worker.getProportionComplete());
		result.put("finished", worker.isFinished());

		if (worker.exceptionOccured()) {
			 logger.error("Galaxy Upload Exception: ", worker.getUploadException());
			result.put("error", worker.getUploadException());
		}

		return result;
	}

}

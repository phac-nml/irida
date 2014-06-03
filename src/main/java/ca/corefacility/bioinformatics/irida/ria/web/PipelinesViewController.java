package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controllers for handling AngularJS request for the Pipelines
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/pipelines/view/")
public class PipelinesViewController {
	private static final Logger logger = LoggerFactory.getLogger(PipelinesViewController.class);

	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String getPipelinesView(){
		logger.debug("Getting pipelines view.");
		return "views/pipelines";
	}
}

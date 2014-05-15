package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controllers for handling AngularJS request for the Dashboard
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/view/")
public class ViewController {
	private static final Logger logger = LoggerFactory.getLogger(ViewController.class);

	@RequestMapping(value = "dashboard", method = RequestMethod.GET)
	public String getDashboardView() {
		logger.debug("Creating partial for dashboard view.");
		return "views/dashboard";
	}
}

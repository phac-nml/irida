package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User Login Page Controller
 * 
 */
@Controller
public class DashboardController {
	private static final String DASHBOARD_PAGE = "dashboard";
	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	/**
	 * Get the index page
	 * @return name of the dashboard view
	 */
	@RequestMapping(value = "/dashboard")
	public String showIndex() {
		logger.debug("Displaying dashboard page");

		return DASHBOARD_PAGE;
	}
}

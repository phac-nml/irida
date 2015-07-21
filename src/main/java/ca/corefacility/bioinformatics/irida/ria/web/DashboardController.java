package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Strings;

/**
 * User Login Page Controller
 * 
 */
@Controller
public class DashboardController {
	private static final String DASHBOARD_PAGE = "index";
	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@Value("${updates.file}")
	private String UPDATE_FILE;

	@RequestMapping(value = "/dashboard")
	public String showIndex(Model model) {
		logger.debug("Displaying dashboard page");
		if (!Strings.isNullOrEmpty(UPDATE_FILE)) {
			model.addAttribute("updates", UPDATE_FILE);
		}
		return DASHBOARD_PAGE;
	}
}

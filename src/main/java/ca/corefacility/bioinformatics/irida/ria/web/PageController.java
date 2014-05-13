package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User Login Page Controller
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class PageController {
	private static final Logger logger = LoggerFactory.getLogger(PageController.class);


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLogin() {
		logger.debug("Displaying login page.");
		return "pages/login/index";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showIndex(final Model model) {
		logger.debug("Displaying dashboard page.");

		model.addAttribute("hello", "Hello IRIDA!");
		return "pages/dashboard/index";
	}
}


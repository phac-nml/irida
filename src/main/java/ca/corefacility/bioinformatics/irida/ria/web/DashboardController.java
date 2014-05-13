package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for Dashboard Page
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class DashboardController {
	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showIndex(final Model model) {
		logger.debug("Displaying dashboard page.");

		model.addAttribute("hello", "Hello IRIDA!");
		return "dashboard/index";
	}
}

package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * User Login Page Controller
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class DashboardController {
	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@RequestMapping(value = "/dashboard")
	public String showIndex(Model model, Principal principal) {
		logger.debug("Displaying dashboard page");
		String name = principal.getName();
		model.addAttribute("name", name);
		return "index";
	}
}

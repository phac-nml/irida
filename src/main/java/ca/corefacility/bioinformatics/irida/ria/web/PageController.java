package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User Login Page Controller
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class PageController {
	private static final Logger logger = LoggerFactory.getLogger(PageController.class);

	@RequestMapping(value = "/app")
	public String showIndex(SitePreference sitePreference, Model model, Principal principal) {
		logger.debug("Displaying dashboard page. With site pref: {}", sitePreference);
		String name = principal.getName();
		model.addAttribute("name", name);
		return "index";
	}
}

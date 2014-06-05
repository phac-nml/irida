package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value = "/")
	public String showSplash(SitePreference sitePreference) {
		logger.debug("Displaying splash page. With site pref: {}", sitePreference);
		return "splash";
	}

	@RequestMapping(value = "/login")
	public String showLogin(Model model, @RequestParam(value = "error", required = false, defaultValue = "false") Boolean hasError) {
		logger.debug("Displaying login page.");
		model.addAttribute("error", hasError);
		return "login";
	}
}

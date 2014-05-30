package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Locale;

/**
 * User Login Page Controller
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class PageController {
	private static final Logger logger = LoggerFactory.getLogger(PageController.class);

	@RequestMapping(value = "/dashboard")
	public String showIndex(SitePreference sitePreference, Model model, Principal principal, Locale locale) {
		logger.debug("Displaying dashboard page. With site pref: {}", sitePreference);
		logger.debug(locale.getDisplayLanguage());
		String name = principal.getName();

		model.addAttribute("name", name);
		return "index";
	}
}

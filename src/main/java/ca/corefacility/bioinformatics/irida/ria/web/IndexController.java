package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Locale;

/**
 * Stand in class to ensure that the web app is running properly.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class IndexController {
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLogin() {
		return "login";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showIndex(final Locale locale, final Model model) {
		logger.debug("Locale is: {}", locale);

		model.addAttribute("hello", "Hello IRIDA!");
		return "dashboard";
	}

}


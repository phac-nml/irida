package ca.corefacility.bioinformatics.irida.ria.web;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;

/**
 */
@Controller
public class LoginController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static final String LOGIN_PAGE = "login";

	@RequestMapping(value = "/")
	public String showSplash() {
		if (isAuthenticated()) {
			return "forward:/dashboard";
		} else {
			return "forward:/login";
		}
	}

	@RequestMapping(value = "/login")
	public String showLogin(Model model,
			@RequestParam(value = "error", required = false, defaultValue = "false") Boolean hasError,
			@RequestParam(value="galaxyCallbackUrl",required=false) String galaxyCallbackURL,
			HttpSession httpSession) {
		logger.debug("Displaying login page.");
		
		//External exporting functionality
		if(galaxyCallbackURL != null) {
			httpSession.setAttribute(ProjectsController.GALAXY_CALLBACK_VARIABLE_NAME, galaxyCallbackURL);
		}
		
		if (isAuthenticated()) {
			return "forward:/dashboard";
		} else {
			model.addAttribute("error", hasError);
			return LOGIN_PAGE;
		}
	}

	private boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken;

	}
}

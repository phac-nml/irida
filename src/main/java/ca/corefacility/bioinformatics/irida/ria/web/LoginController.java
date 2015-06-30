package ca.corefacility.bioinformatics.irida.ria.web;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.ria.utilities.EmailController;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;

/**
 */
@Controller
public class LoginController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static final String LOGIN_PAGE = "login";
	
	
	private final EmailController emailController;
	
	@Autowired
	public LoginController(final EmailController emailController) {
		this.emailController = emailController;
	}

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
			@RequestParam(value="galaxyClientID",required=false) String galaxyClientID,
			HttpSession httpSession) {
		logger.debug("Displaying login page.");
		
		model.addAttribute("emailConfigured", emailController.isMailConfigured());
		
		//External exporting functionality
		if(galaxyCallbackURL != null && galaxyClientID !=null) {
			httpSession.setAttribute(ProjectsController.GALAXY_CALLBACK_VARIABLE_NAME, galaxyCallbackURL);
			httpSession.setAttribute(ProjectsController.GALAXY_CLIENT_ID_NAME, galaxyClientID);
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

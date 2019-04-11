package ca.corefacility.bioinformatics.irida.ria.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.service.EmailController;

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

	/**
	 * Get the index page
	 * @return redirect to the dashboard page
	 */
	@RequestMapping(value = "/")
	public String showSplash() {
		return "forward:/dashboard";
	}

	/**
	 * Get the login page
	 *
	 * @param model model for the view
	 * @param hasError Whether there's a login error
	 * @param principal Currently logged in user.  If set user will get sent to dashboard.
	 * @return Login page view name
	 */
	@RequestMapping(value = "/login")
	public String showLogin(Model model,
			@RequestParam(value = "error", required = false, defaultValue = "false") Boolean hasError,
			Principal principal) {

		if (principal != null) {
			logger.debug("User is already logged in.");
			return "forward:/dashboard";
		}

		logger.debug("Displaying login page.");

		model.addAttribute("emailConfigured", emailController.isMailConfigured());
		model.addAttribute("error", hasError);
		return LOGIN_PAGE;
	}
}

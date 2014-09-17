package ca.corefacility.bioinformatics.irida.ria.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
public class LoginController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static final String SPLASH_PAGE = "themes/wet/splash";
	private static final String LOGIN_PAGE = "login";


	@RequestMapping(value = "/")
	public String showSplash() {
		if (Strings.isNullOrEmpty(theme)) {
			theme = WET_BOEW_THEME;
		}
		if (isAuthenticated()) {
			return "forward:/dashboard";
		} else {
			if(theme.equals(WET_BOEW_THEME)){
				// Only need the language page for the wet theme.
				return SPLASH_PAGE;
			}
			else {
				return "forward:/login";
			}

		}
	}

	@RequestMapping(value = "/login")
	public String showLogin(Model model,
			@RequestParam(value = "error", required = false, defaultValue = "false") Boolean hasError) {
		logger.debug("Displaying login page.");
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

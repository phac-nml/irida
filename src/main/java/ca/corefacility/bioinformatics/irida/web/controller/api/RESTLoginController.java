package ca.corefacility.bioinformatics.irida.web.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to handle logins for the REST API
 */
@Controller
public class RESTLoginController {

	/**
	 * Return the login view
	 * @return the login view name
	 */
	@RequestMapping("/api/login")
	public String login(){
		return "oauth/login-api";
	}

	/**
	 * Return a success message if logged in
	 * @return A success message
	 */
	@RequestMapping("/api/success")
	@ResponseBody
	public String success(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return "Logged in as " + authentication.getName();
	}
}

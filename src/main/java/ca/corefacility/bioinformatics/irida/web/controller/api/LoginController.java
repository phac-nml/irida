package ca.corefacility.bioinformatics.irida.web.controller.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
	@RequestMapping("/api/login")
	public String login(){
		return "login";
	}
	
	@RequestMapping("/api/success")
	@ResponseBody
	public String success(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return "Logged in as " + authentication.getName();
	}
}

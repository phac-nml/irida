package ca.corefacility.bioinformatics.irida.example.oauthClient.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@Autowired
	OltuAuthorizationController authController;
	
	@RequestMapping(value = "/")
	public ModelAndView test(HttpServletResponse response) throws IOException {
		return new ModelAndView("home");
	}

	@RequestMapping(value = "/choose")
	public ModelAndView choose(@RequestParam String serviceURI, @RequestParam String clientId, @RequestParam String clientSecret) throws OAuthSystemException {
		return authController.authenticate(serviceURI, clientId, clientSecret, "http://localhost:8181/data");
	}
	
	@RequestMapping(value="/data")
	@ResponseBody
	public String data(@RequestParam String token){
		return "Token is " + token;
	}
}

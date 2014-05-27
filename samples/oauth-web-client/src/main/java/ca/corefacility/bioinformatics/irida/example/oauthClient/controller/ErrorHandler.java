package ca.corefacility.bioinformatics.irida.example.oauthClient.controller;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;

@ControllerAdvice
@Scope("session")
public class ErrorHandler {
	private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

	// a reference to the authorization controller
	private OltuAuthorizationController authController;
	
	@Autowired
	public ErrorHandler(OltuAuthorizationController authController) {
		this.authController = authController;
	}
	
	@ExceptionHandler(IridaOAuthException.class)
	public ModelAndView handleOAuthException(HttpServletRequest request, IridaOAuthException ex)
			throws OAuthSystemException, MalformedURLException {
		logger.debug("Caught IridaOAuthException.  Beginning OAuth2 authentication token flow.");
		String requestURI = request.getRequestURI();

		return authController.authenticate(ex.getRemoteAPI(), requestURI);
	}
	
	@ExceptionHandler(OAuthProblemException.class)
	public  ModelAndView handleOAuthProblemException(OAuthProblemException ex) {
			logger.error("OAuth exception: " + ex.getMessage());
			
			ModelAndView modelAndView = new ModelAndView("oauth_error");
			modelAndView.addObject("exception",ex);
			return modelAndView;
	}
}

package ca.corefacility.bioinformatics.irida.example.oauthClient.controller;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
public class ErrorHandler {
	private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

	@ExceptionHandler(OAuthProblemException.class)
	public  ModelAndView handleOAuthProblemException(OAuthProblemException ex) {
			logger.error("OAuth exception: " + ex.getMessage());
			
			ModelAndView modelAndView = new ModelAndView("oauth_error");
			modelAndView.addObject("exception",ex);
			return modelAndView;
	}
}

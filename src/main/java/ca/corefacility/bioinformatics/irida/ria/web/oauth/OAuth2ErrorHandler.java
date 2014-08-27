package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ca.corefacility.bioinformatics.irida.exceptions.oauth.IridaOAuthException;

@ControllerAdvice
public class OAuth2ErrorHandler {
	private static final Logger logger = LoggerFactory.getLogger(OAuth2ErrorHandler.class);

	// a reference to the authorization controller
	private final OltuAuthorizationController authController;

	@Autowired
	public OAuth2ErrorHandler(OltuAuthorizationController authController) {
		this.authController = authController;
	}

	@ExceptionHandler(IridaOAuthException.class)
	public String handleOAuthException(HttpServletRequest request, IridaOAuthException ex) throws OAuthSystemException,
			MalformedURLException {
		logger.debug("Caught IridaOAuthException.  Beginning OAuth2 authentication token flow.");
		String requestURI = request.getRequestURI();

		return authController.authenticate(ex.getRemoteAPI(), requestURI);
	}

	@ExceptionHandler(OAuthProblemException.class)
	public String handleOAuthProblemException(OAuthProblemException ex, Model model) {
		logger.error("OAuth exception: " + ex.getMessage());

		model.addAttribute("exception", ex);
		return "oauth_error";
	}
}

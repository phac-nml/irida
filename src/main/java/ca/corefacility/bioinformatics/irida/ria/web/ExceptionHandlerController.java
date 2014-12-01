package ca.corefacility.bioinformatics.irida.ria.web;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;

/**
 * Created by josh on 2014-07-15.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@ControllerAdvice
public class ExceptionHandlerController {
	public static final String NOT_FOUND_PAGE = "errors/not_found";
	public static final String ACCESS_DENIED_PAGE = "errors/access_denied";
	private static final String OTHER_ERROR_PAGE = "errors/error";
	private static final String OAUTH_ERROR_PAGE = "errors/oauth_error";
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public String handleResourceNotFoundException(EntityNotFoundException ex) {
		logger.error(ex.getMessage(), ex);
		return NOT_FOUND_PAGE;
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public String handleAccessDeniedException(AccessDeniedException ex) {
		logger.error(ex.getMessage(), ex);
		return ACCESS_DENIED_PAGE;
	}

	@ExceptionHandler(OAuthProblemException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleOAuthProblemException(OAuthProblemException ex) {
		logger.error("OAuth exception: " + ex.getMessage(), ex);

		ModelAndView modelAndView = new ModelAndView(OAUTH_ERROR_PAGE);
		modelAndView.addObject("exception", ex);
		return modelAndView;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleOtherExceptions(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return OTHER_ERROR_PAGE;
	}
}

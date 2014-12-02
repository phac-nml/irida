package ca.corefacility.bioinformatics.irida.ria.web.errors;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
@ControllerAdvice(basePackages = "ca.corefacility.bioinformatics.irida.ria.web")
public class ExceptionHandlerController {
	public static final String NOT_FOUND_PAGE = "errors/not_found";
	public static final String ACCESS_DENIED_PAGE = "errors/access_denied";
	private static final String OTHER_ERROR_PAGE = "errors/error";
	private static final String OAUTH_ERROR_PAGE = "errors/oauth_error";
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@Value("${mail.server.email}")
	private String adminEmail;

	/**
	 * Handle an {@link EntityNotFoundException} and return an http 404
	 * 
	 * @param ex
	 *            The EntityNotFoundException caught
	 * @return the name of the not_found view
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ModelAndView handleResourceNotFoundException(EntityNotFoundException ex) {
		logger.error(ex.getMessage(), ex);
		ModelAndView modelAndView = new ModelAndView(NOT_FOUND_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	/**
	 * Handle an {@link AccessDeniedException} and return an Http 403
	 * 
	 * @param ex
	 *            The caught {@link AccessDeniedException}
	 * @return name of the access denied view
	 */
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
		logger.error(ex.getMessage(), ex);
		ModelAndView modelAndView = new ModelAndView(ACCESS_DENIED_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	/**
	 * Catch an {@link OAuthProblemException} and return an http 500 error
	 * 
	 * @param ex
	 *            the caught {@link OAuthProblemException}
	 * @return A {@link ModelAndView} containing the name of the oauth error
	 *         view
	 */
	@ExceptionHandler(OAuthProblemException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleOAuthProblemException(OAuthProblemException ex) {
		logger.error("OAuth exception: " + ex.getMessage(), ex);

		ModelAndView modelAndView = new ModelAndView(OAUTH_ERROR_PAGE);
		modelAndView.addObject("exception", ex);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	/**
	 * Catch all other exception types and display a generic error page and 500
	 * error
	 * 
	 * @param ex
	 *            The caught exception
	 * @return Name of the generic error page
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleOtherExceptions(Exception ex) {
		logger.error(ex.getMessage(), ex);
		ModelAndView modelAndView = new ModelAndView(OTHER_ERROR_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}
}

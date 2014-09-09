package ca.corefacility.bioinformatics.irida.ria.web;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;

/**
 * Created by josh on 2014-07-15.
 */
@ControllerAdvice
public class ExceptionHandlerController {
    private static final String ERROR_PAGE = "error";
    private static final String OAUTH_ERROR_PAGE = "oauth_error";
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleResourceNotFoundException(EntityNotFoundException ex) {
        // TODO: (Josh - 2014-07-15) Create a page for this
    	logger.error(ex.toString());
    	ex.printStackTrace();
        return ERROR_PAGE;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex) {
        // TODO: (Josh - 2014-07-15) Create a page for this
    	logger.error(ex.toString());
    	ex.printStackTrace();
        return ERROR_PAGE;
    }
    
    @ExceptionHandler(OAuthProblemException.class)
	public String handleOAuthProblemException(OAuthProblemException ex, Model model) {
		logger.error("OAuth exception: " + ex.getMessage());

		model.addAttribute("exception", ex);
		return OAUTH_ERROR_PAGE;
	}
}

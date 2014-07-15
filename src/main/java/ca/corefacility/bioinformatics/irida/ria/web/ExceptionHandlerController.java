package ca.corefacility.bioinformatics.irida.ria.web;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by josh on 2014-07-15.
 */
@ControllerAdvice
public class ExceptionHandlerController {
    private static final String ERROR_PAGE = "error";

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleResourceNotFoundException() {
        // TODO: (Josh - 2014-07-15) Create a page for this
        return ERROR_PAGE;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException() {
        // TODO: (Josh - 2014-07-15) Create a page for this
        return ERROR_PAGE;
    }
}

package ca.corefacility.bioinformatics.irida.web.controller.test.unit.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.ControllerExceptionHandler;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.ErrorResponse;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestEntity;

/**
 * Unit tests for {@link ca.corefacility.bioinformatics.irida.web.controller.api.exception.ControllerExceptionHandler}
 *
 */
public class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler controller;

    @Before
    public void setUp() {
        controller = new ControllerExceptionHandler();
    }

    @Test
    public void testHandleConstraintViolations() {
    	final String MESSAGES_BASENAME = "ValidationMessages";
		Configuration<?> configuration = Validation.byDefaultProvider().configure();
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename(MESSAGES_BASENAME);
		configuration.messageInterpolator(new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator(
				MESSAGES_BASENAME)));
		ValidatorFactory factory = configuration.buildValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        Set<ConstraintViolation<IdentifiableTestEntity>> violations = validator.validate(new IdentifiableTestEntity());
        for (ConstraintViolation<IdentifiableTestEntity> v : violations) {
            constraintViolations.add(v);
        }
        ResponseEntity<Map<String, List<String>>> response = controller.handleConstraintViolations(
                new ConstraintViolationException(constraintViolations));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        //assertEquals("{\"label\":[\"You must provide a label.\"]}", response.getBody());
        Map<String, List<String>> body = response.getBody();
        assertTrue("The response must contain an error about a missing label.", body.containsKey("label"));
        List<String> labels = body.get("label");
        assertEquals("There must only be one error with the label.", 1, labels.size());
        String error = labels.get(0);
        assertEquals("The error must be 'You must provide a label.'", "You must provide a label.", error);
    }

    @Test
    public void testHandleNotFoundException() {
        ResponseEntity<ErrorResponse> response = controller.handleNotFoundException(new EntityNotFoundException("not found"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testHandleArgumentException() {
        ResponseEntity<ErrorResponse> response = controller.handleArgumentException(new IllegalArgumentException("illegal argument"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testHandleExistsException() {
        ResponseEntity<ErrorResponse> response = controller.handleExistsException(new EntityExistsException("exists"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testHandleInvalidPropertyException() {
        ResponseEntity<ErrorResponse> response = controller.handleInvalidPropertyException(
                new InvalidPropertyException("invalid property"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testHandleOtherExceptions() {
        ResponseEntity<ErrorResponse> response = controller.handleAllOtherExceptions(new Exception("exception"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}

package ca.corefacility.bioinformatics.irida.web.controller.test.unit.exception;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.ControllerExceptionHandler;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.support.IdentifiableTestEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link ca.corefacility.bioinformatics.irida.web.controller.api.exception.ControllerExceptionHandler}
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler controller;

    @Before
    public void setUp() {
        controller = new ControllerExceptionHandler();
    }

    @Test
    public void testHandleConstraintViolations() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        Set<ConstraintViolation<IdentifiableTestEntity>> violations = validator.validate(new IdentifiableTestEntity());
        for (ConstraintViolation<IdentifiableTestEntity> v : violations) {
            constraintViolations.add(v);
        }
        ResponseEntity<String> response = controller.handleConstraintViolations(
                new ConstraintViolationException(constraintViolations));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"label\":[\"may not be null\"]}", response.getBody());
    }

    @Test
    public void testHandleNotFoundException() {
        ResponseEntity<String> response = controller.handleNotFoundException(new EntityNotFoundException("not found"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testHandleExistsException() {
        ResponseEntity<String> response = controller.handleExistsException(new EntityExistsException("exists"));
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testHandleInvalidPropertyException() {
        ResponseEntity<String> response = controller.handleInvalidPropertyException(
                new InvalidPropertyException("invalid property"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testHandleOtherExceptions() {
        ResponseEntity<String> response = controller.handleAllOtherExceptions(new Exception("exception"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}

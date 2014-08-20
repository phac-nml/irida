package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Base class for controllers so that they all can have access to common functionality.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class BaseController {
	protected static final String MODEL_ERROR_ATTR = "errors";

	/**
	 * Changes a {@link javax.validation.ConstraintViolationException} to a usable map of strings
	 * for displaing in the UI.
	 *
	 * @param e {@link javax.validation.ConstraintViolationException} for the form submitted.
	 * @return Map of string {fieldName, error}
	 */
	protected Map<String, String> getErrorsFromViolationException(ConstraintViolationException e) {
		Map<String, String> errors = new HashMap<>();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			String message = violation.getMessage();
			String field = violation.getPropertyPath().toString();
			errors.put(field, message);
		}
		return errors;
	}
}

package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;

import ca.corefacility.bioinformatics.irida.ria.utilities.ExceptionPropertyAndMessage;

/**
 * Base class for controllers so that they all can have access to common
 * functionality.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class BaseController {
	protected static final String MODEL_ERROR_ATTR = "errors";

	/**
	 * Changes a {@link javax.validation.ConstraintViolationException} to a
	 * usable map of strings for displaying in the UI.
	 *
	 * @param e
	 *            {@link javax.validation.ConstraintViolationException} for the
	 *            form submitted.
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

	/**
	 * Converts a DataIntegrityViolationException to a usable map of strings for
	 * displaying in the UI.
	 * 
	 * @param e
	 *            The exception for the form submitted
	 * @param messageNames
	 *            A map of the error message names
	 * @param messageSource
	 *            A message source to get the messages from
	 * @param locale
	 *            The locale of the request
	 * @return A Map of strings with the property names and error messages
	 */
	protected Map<String, String> getErrorsFromDataIntegrityViolationException(DataIntegrityViolationException e,
			Map<String, ExceptionPropertyAndMessage> messageNames, MessageSource messageSource, Locale locale) {
		Map<String, String> errors = new HashMap<>();
		boolean found = false;
		for (String errorName : messageNames.keySet()) {
			if (e.getMessage().contains(errorName)) {
				ExceptionPropertyAndMessage val = messageNames.get(errorName);
				errors.put(val.getPropertyName(), messageSource.getMessage(val.getMessageName(), null, locale));
				found = true;
			}
		}

		if (!found) {
			throw e;
		}

		return errors;
	}
}

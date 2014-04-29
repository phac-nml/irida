package ca.corefacility.bioinformatics.irida.web.controller.api.exception;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.*;

/**
 * Globally handles exceptions thrown by controllers.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@ControllerAdvice
public class ControllerExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	private static final MediaType[] ACCEPTABLE_MEDIA_TYPES_ARRAY = new MediaType[] { MediaType.APPLICATION_JSON,
			MediaType.APPLICATION_XML };
	private static final String ACCEPTABLE_MEDIA_TYPES = Arrays.toString(ACCEPTABLE_MEDIA_TYPES_ARRAY);

	/**
	 * Handle {@link Exception}.
	 * 
	 * @param e
	 *            the exception as thrown by the service.
	 * @return an appropriate HTTP response.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception e) {
		logger.error("An exception happened at " + new Date() + ". The stack trace follows: ", e);
		return new ResponseEntity<>(new ErrorResponse("Server error."), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle
	 * {@link ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException}
	 * .
	 * 
	 * @param e
	 *            the exception as thrown by the service.
	 * @return an appropriate HTTP response.
	 */
	@ExceptionHandler(InvalidPropertyException.class)
	public ResponseEntity<ErrorResponse> handleInvalidPropertyException(InvalidPropertyException e) {
		logger.error("A client attempted to update a resource with an" + " invalid property at " + new Date()
				+ ". The stack trace follows: ", e);
		return new ResponseEntity<>(new ErrorResponse("Cannot update resource with supplied properties."), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle
	 * {@link ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException}
	 * .
	 * 
	 * @param e
	 *            the exception as thrown by the service.
	 * @return an appropriate HTTP response.
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundException(EntityNotFoundException e) {
		logger.info("A client attempted to retrieve a resource with an identifier that does not exist at "
				+ new Date() + ".");
		return new ResponseEntity<>(new ErrorResponse("No such resource found."), HttpStatus.NOT_FOUND);
	}

	/**
	 * Handle {@link javax.validation.ConstraintViolationException}.
	 * 
	 * @param e
	 *            the exception as thrown by the service.
	 * @return an appropriate HTTP response.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<String> handleConstraintViolations(ConstraintViolationException e) {
		Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			constraintViolations.add(violation);
		}
		logger.info("A client attempted to create or update a resource with invalid values at " + new Date());
		return new ResponseEntity<>(validationMessages(constraintViolations), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle
	 * {@link ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException}
	 * .
	 * 
	 * @param e
	 *            the exception as thrown by the service.
	 * @return an appropriate HTTP response.
	 */
	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<ErrorResponse> handleExistsException(EntityExistsException e) {
		logger.info("A client attempted to create a new resource with an identifier that exists, "
				+ "or modify a resource to have an identifier that already exists at " + new Date());
		String message = "An entity already exists with that identifier.";
		return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.CONFLICT);
	}

	/**
	 * Handle {@link HttpRequestMethodNotSupportedException}.
	 * 
	 * @param e
	 *            the exception as thrown by Spring.
	 * @return an appropriate HTTP response.
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		logger.error("A client attempted to issue a request against an endpoint with an unsupported method: ["
				+ e.getMethod() + "]");
		String message = "This method is not supported at this endpoint.";
		return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
		logger.error("A client attempted to issue a data submission against an endpoint with an unsupported "
				+ "media type: [" + e.getContentType() + "]");
		String message = "The content type you provided is not supported by this resource."
				+ " Resources generally support " + ACCEPTABLE_MEDIA_TYPES;
		return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	/**
	 * Handle {@link com.fasterxml.jackson.core.JsonParseException}.
	 * 
	 * @param e
	 *            the exception as thrown by the JSON parser.
	 * @return an appropriate HTTP response.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleInvalidJsonException(HttpMessageNotReadableException e) {
		logger.debug("Client attempted to send invalid JSON.");
		String message = "Your request could not be parsed.";
		Throwable cause = e.getCause();
		if (cause instanceof UnrecognizedPropertyException) {
			// this is thrown when Jackson tries to de-serialize JSON into an
			// object and the JSON object has a field that
			// doesn't exist
			UnrecognizedPropertyException unrecognizedProperty = (UnrecognizedPropertyException) cause;
			String propertyName = unrecognizedProperty.getUnrecognizedPropertyName();
			Collection<Object> acceptableProperties = unrecognizedProperty.getKnownPropertyIds();
			StringBuilder builder = new StringBuilder("Unrecognized property [");
			builder.append(propertyName)
					.append("] in JSON request. The object that you were trying to create or update accepts the following fields: [\n");
			for (Object acceptableProperty : acceptableProperties) {
				// DON'T append the links entry
				if (!acceptableProperty.equals("links")) {
					builder.append(acceptableProperty).append(",\n");
				}
			}
			builder.append("].");
			message = builder.toString();
			logger.debug("Sending the following message to the client: [" + message + "]");
		} else if (cause instanceof JsonParseException) {
			logger.debug("Client attempted to send JSON with the wrong type of double quotes.");
			JsonParseException parseException = (JsonParseException) cause;
			if (parseException.getMessage().contains("double-quote")) {
				message = "Your request could not be parsed. Field names must be surrounded by double quotes (see: http://www.json.org/).";
			} else {
				message = "Your request could not be parsed for an unknown reason."
						+ " The message we got from the JSON parsing library follows, maybe that will help debug your JSON: ["
						+ e.getMessage() + "]";

			}
		}

		return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle {@link AccessDeniedException}.
	 * 
	 * @param e
	 *            the exception thrown by spring security.
	 * @return a forbidden response.
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
		ErrorResponse resp = new ErrorResponse("You do not have permissions to perform this action.");
		return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
	}

	/**
	 * Render a collection of constraint violations as a JSON object.
	 * 
	 * @param failures
	 *            the set of constraint violations.
	 * @return the constraint violations as a JSON object.
	 */
	private String validationMessages(Set<ConstraintViolation<?>> failures) {
		Map<String, List<String>> mp = new HashMap<>();
		for (ConstraintViolation<?> failure : failures) {
			logger.debug(failure.getPropertyPath().toString() + ": " + failure.getMessage());
			String property = failure.getPropertyPath().toString();
			if (mp.containsKey(property)) {
				mp.get(failure.getPropertyPath().toString()).add(failure.getMessage());
			} else {
				List<String> list = new ArrayList<>();
				list.add(failure.getMessage());
				mp.put(property, list);
			}
		}
		return new Gson().toJson(mp);
	}
}

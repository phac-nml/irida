package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.ria.utilities.ExceptionPropertyAndMessage;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxFormErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

import com.google.common.collect.ImmutableMap;

@RestController
@RequestMapping("/ajax/remote_api")
public class RemoteApiAjaxController extends BaseController {
	private RemoteAPIService remoteAPIService;
	private MessageSource messageSource;

	// Map storing the message names for the
	// getErrorsFromDataIntegrityViolationException method
	private final Map<String, ExceptionPropertyAndMessage> errorMessages = ImmutableMap.of(
			RemoteAPI.SERVICE_URI_CONSTRAINT_NAME,
			new ExceptionPropertyAndMessage("serviceURI", "remoteapi.create.serviceURIConflict"));

	@Autowired
	public void setRemoteAPIService(RemoteAPIService service) {
		this.remoteAPIService = service;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Create a new client
	 *
	 * @param client The client to add
	 * @param locale Locale of the current user session
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping(value = "/create")
	public ResponseEntity<AjaxResponse> postCreateRemoteAPI(RemoteAPI client, Locale locale) {
		Map<String, String> errors = new HashMap<>();
		try {
			RemoteAPI remoteAPI = remoteAPIService.create(client);
			return ResponseEntity.ok(new AjaxCreateItemSuccessResponse(remoteAPI.getId()));
		} catch (ConstraintViolationException e) {
			errors = getErrorsFromViolationException(e);
		} catch (DataIntegrityViolationException e) {
			errors = getErrorsFromDataIntegrityViolationException(e, errorMessages, messageSource, locale);
		}
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new AjaxFormErrorResponse(errors));
	}
}

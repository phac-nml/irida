package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients.ClientTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients.CreateUpdateClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIClientService;

/**
 * Controller to handle ajax request for IRIDA Clients.
 */
@RestController
@RequestMapping("/ajax/clients")
public class ClientsAjaxController {
	private final UIClientService service;
	private final MessageSource messageSource;

	@Autowired
	public ClientsAjaxController(UIClientService service, MessageSource messageSource) {
		this.service = service;
		this.messageSource = messageSource;
	}

	/**
	 * Get a page in the clients listing table based on the table request.
	 *
	 * @param request Information about the current location in the Clients table
	 * @return The current page of the clients table
	 */
	@RequestMapping("/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<TableResponse<ClientTableModel>> getClientsList(@RequestBody ClientTableRequest request) {
		return ResponseEntity.ok(service.getClientList(request));
	}

	/**
	 * Revoke all tokens for a client given its identifier
	 *
	 * @param id Identifier for a specific client
	 */
	@DeleteMapping("/revoke")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteClientTokens(@RequestParam Long id) {
		service.deleteClientTokens(id);
	}

	/**
	 * Check to see if the client identifier that a user wants to use for a new client is not already used
	 *
	 * @param clientId Name to check if exists
	 * @param locale   Users current locale
	 * @return Http response indicating if the client id is valid.
	 */
	@RequestMapping("/validate")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> validateClientName(@RequestParam String clientId, Locale locale) {
		try {
			service.validateClientId(clientId);
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(messageSource.getMessage("server.AddClientForm.error", new Object[] { clientId }, locale));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.ok("");
		}
	}

	/**
	 * Create a new client.
	 *
	 * @param request Details about the client to create
	 * @param locale  users current locale
	 * @return Http response containing the result
	 */
	@PostMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<AjaxResponse> createClient(@RequestBody CreateUpdateClientDetails request, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(service.createOrUpdateClient(request, locale)));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new AjaxErrorResponse(messageSource.getMessage("server.AddClientForm.error",
							new Object[] { request.getClientId() }, locale)));
		}
	}

	/**
	 * Update the details of a client
	 *
	 * @param request Updated details about a client.
	 * @param locale  Current users locale
	 * @return Client id if success or an error message if there was an error during the update
	 */
	@PutMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<AjaxResponse> updateClient(@RequestBody CreateUpdateClientDetails request, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(service.createOrUpdateClient(request, locale)));
		} catch (Exception exception) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new AjaxErrorResponse(messageSource.getMessage("server.UpdateClientForm.error",
							new Object[] { request.getClientId() }, locale)));
		}
	}

	/**
	 * Delete a client
	 *
	 * @param id identifier for a client to delete
	 */
	@DeleteMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteClient(@RequestParam Long id) {
		service.deleteClient(id);
	}

	/**
	 * Create a secret for a client
	 *
	 * @param id identifier for the client to update.
	 */
	@PutMapping("/secret")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void regenerateClientSecret(@RequestParam Long id) {
		service.regenerateClientSecret(id);
	}
}

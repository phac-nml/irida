package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateClientRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIClientService;

/**
 * Controller to handle ajax request for IRIDA Clients.
 */
@RestController
@RequestMapping("/ajax/clients")
public class ClientsAjaxController {
	private final UIClientService service;

	@Autowired
	public ClientsAjaxController(UIClientService service) {
		this.service = service;
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

	@PostMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Long> createClient(@RequestBody CreateClientRequest request) {
		return ResponseEntity.ok(service.createClient(request));
	}
}

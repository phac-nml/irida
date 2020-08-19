package ca.corefacility.bioinformatics.irida.ria.web.clients;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientCreateModel;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientModel;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIClientService;

/**
 * Controller for all ajax requests from the UI for clients.
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
	 * Get a {@link TableResponse} for the Clients page.
	 *
	 * @param tableRequest
	 * 		{@link TableRequest} for the current clients table.
	 *
	 * @return {@link TableResponse}
	 */
	@RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TableResponse<ClientModel>> getAjaxClientsList(@RequestBody ClientTableRequest tableRequest) {
		return ResponseEntity.ok(service.getPagedClientsList(tableRequest));
	}

	@GetMapping("/create")
	public ResponseEntity<ClientCreateModel> getCreateClientDetails(Locale locale) {
		return ResponseEntity.ok(service.getClientCreateDetails(locale));
	}
}

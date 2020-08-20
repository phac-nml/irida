package ca.corefacility.bioinformatics.irida.ria.web.ajax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIClientService;

@RestController
@RequestMapping("/ajax/clients")
public class ClientsAjaxController {
	private final UIClientService service;

	@Autowired
	public ClientsAjaxController(UIClientService service) {
		this.service = service;
	}

	@RequestMapping("/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<TableResponse<ClientTableModel>> getClientsList(@RequestBody ClientTableRequest request) {
		return ResponseEntity.ok(service.getClientList(request));
	}

}

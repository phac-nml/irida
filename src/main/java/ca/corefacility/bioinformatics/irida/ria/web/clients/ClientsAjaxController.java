package ca.corefacility.bioinformatics.irida.ria.web.clients;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.repositories.specification.IridaClientDetailsSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientModel;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

/**
 * Controller for all ajax requests from the UI for clients.
 */
@RestController
@RequestMapping("/ajax/clients")
public class ClientsAjaxController {
	private final IridaClientDetailsService clientDetailsService;

	@Autowired
	public ClientsAjaxController(IridaClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
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
	@ResponseBody
	public TableResponse<ClientModel> getAjaxClientsList(@RequestBody ClientTableRequest tableRequest) {
		Specification<IridaClientDetails> specification = IridaClientDetailsSpecification
				.searchClient(tableRequest.getSearch());

		Page<IridaClientDetails> page = clientDetailsService
				.search(specification, PageRequest.of(tableRequest.getCurrent(), tableRequest.getPageSize(), tableRequest.getSort()));
		List<ClientModel> models = page.getContent().stream().map(client -> new ClientModel(client, clientDetailsService.countActiveTokensForClient(client)))
				.collect(Collectors.toList());

		return new TableResponse<ClientModel>(models, page.getTotalElements());
	}
}

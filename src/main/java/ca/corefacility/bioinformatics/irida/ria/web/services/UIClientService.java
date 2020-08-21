package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.repositories.specification.IridaClientDetailsSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

/**
 * UI Service to handle IRIDA Clients
 */
@Component
public class UIClientService {
	private final IridaClientDetailsService clientDetailsService;

	public UIClientService(IridaClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}

	/**
	 * Get a listing of clients based on the table request.
	 *
	 * @param tableRequest Information about the sort and page of the table.
	 * @return Current status of the table
	 */
	public TableResponse<ClientTableModel> getClientList(ClientTableRequest tableRequest) {
		Specification<IridaClientDetails> specification = IridaClientDetailsSpecification.searchClient(
				tableRequest.getSearch());

		Page<IridaClientDetails> page = clientDetailsService.search(specification,
				PageRequest.of(tableRequest.getCurrent(), tableRequest.getPageSize(), tableRequest.getSort()));
		List<ClientTableModel> models = page.getContent()
				.stream()
				.map(client -> new ClientTableModel(client, clientDetailsService.countActiveTokensForClient(client)))
				.collect(Collectors.toList());

		return new TableResponse<>(models, page.getTotalElements());
	}
}

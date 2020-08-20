package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.repositories.specification.IridaClientDetailsSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientCreateModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.generic.LabelAndValue;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientModel;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class UIClientService {
	private final IridaClientDetailsService clientDetailsService;
	private final MessageSource messageSource;

	private final List<Integer> AVAILABLE_REFRESH_TOKEN_VALIDITY = Lists.newArrayList(
			0,
			// 7 days
			604800,
			// 1 month
			2592000,
			// 3 months
			7776000,
			// 6 months
			15552000);

	private final List<Integer> AVAILABLE_TOKEN_VALIDITY = Lists.newArrayList(
			// 30 minutes
			1800,
			// 1 hour
			3600,
			// 2 hours
			7200,
			// 6 hours
			21600,
			// 12 hours
			43200,
			// 1 day
			86400,
			// 2 days
			172800,
			// 7 days
			604800);

	@Autowired
	public UIClientService(IridaClientDetailsService clientDetailsService, MessageSource messageSource) {
		this.clientDetailsService = clientDetailsService;
		this.messageSource = messageSource;
	}

	public TableResponse<ClientModel> getPagedClientsList(ClientTableRequest request) {
		Specification<IridaClientDetails> specification = IridaClientDetailsSpecification.searchClient(
				request.getSearch());

		Page<IridaClientDetails> page = clientDetailsService.search(specification,
				PageRequest.of(request.getCurrent(), request.getPageSize(), request.getSort()));
		List<ClientModel> models = page.getContent()
				.stream()
				.map(client -> new ClientModel(client, clientDetailsService.countActiveTokensForClient(client)))
				.collect(Collectors.toList());

		return new TableResponse<>(models, page.getTotalElements());
	}

	public ClientCreateModel getClientCreateDetails(Locale locale) {
		List<LabelAndValue> refreshTokenValidity = AVAILABLE_REFRESH_TOKEN_VALIDITY.stream()
				.map(time -> new LabelAndValue(
						messageSource.getMessage("server.AddClientForm.refreshTokenValidity." + time, null, locale),
						String.valueOf(time)))
				.collect(Collectors.toList());

		List<LabelAndValue> tokenValidity = AVAILABLE_TOKEN_VALIDITY.stream()
				.map(time -> new LabelAndValue(
						messageSource.getMessage("server.AddClientForm.tokenValidity." + time, null, locale),
						String.valueOf(time)))
				.collect(Collectors.toList());

		return new ClientCreateModel(refreshTokenValidity, tokenValidity);
	}

	public void createNewClient(Locale locale) {

	}
}

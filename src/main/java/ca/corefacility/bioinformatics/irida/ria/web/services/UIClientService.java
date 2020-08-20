package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.repositories.specification.IridaClientDetailsSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientCreateModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ClientDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.CreateClientRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.generic.LabelAndValue;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientModel;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

import com.google.common.collect.Lists;

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

	public ClientDetailsResponse getClientDetails(Long clientId) {
		IridaClientDetails details = clientDetailsService.read(clientId);

		int totalTokens = clientDetailsService.countTokensForClient(details);
		int activeTokens = clientDetailsService.countActiveTokensForClient(details);

		return new ClientDetailsResponse(details, totalTokens, activeTokens);
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

	public long createNewClient(CreateClientRequest request, Locale locale) {
		IridaClientDetails clientDetails = new IridaClientDetails();
		clientDetails.setClientId(request.getClientId());
		clientDetails.setClientSecret(RandomStringUtils.randomAlphanumeric(42));

		// TOKEN VALIDITY
		clientDetails.setAccessTokenValiditySeconds(request.getTokenValidity());

		// GRANT TYPE
		clientDetails.getAuthorizedGrantTypes()
				.add(request.getGrantType());
		if (request.getGrantType()
				.equals("authorization_code")) {
			request.setRedirectURI(request.getRedirectURI());
		}

		Set<String> scopes = new HashSet<>();
		Set<String> autoScopes = new HashSet<>();

		// SCOPE READ
		final String scopeRead = request.getScopeRead();
		final String scopeWrite = request.getScopeWrite();
		if (scopeRead
				.equals("read")) {
			scopes.add("read");
		} else if (scopeRead
				.equals("auto")) {
			scopes.add("read");
			autoScopes.add("read");
		}

		// SCOPE WRITE
		if (scopeWrite.equals("write")) {
			scopes.add("write");
		} else if (scopeWrite.equals("auto")) {
			scopes.add("write");
			autoScopes.add("write");
		}
		clientDetails.setScope(scopes);
		clientDetails.setAutoApprovableScopes(autoScopes);

		// REFRESH TOKEN
		if (request.getRefreshTokenValidity() > 0) {
			clientDetails.getAuthorizedGrantTypes()
					.add("refresh_token");
			clientDetails.setRefreshTokenValiditySeconds(request.getRefreshTokenValidity());
		}

		clientDetails = clientDetailsService.create(clientDetails);

		return clientDetails.getId();
	}
}

package ca.corefacility.bioinformatics.irida.oauth2;

import java.time.Duration;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.repositories.IridaClientDetailsRepository;

/**
 * A converter implementation of {@link RegisteredClientRepository}, that transforms {@link IridaClientDetails} to/from
 * {@link RegisteredClient}.
 */
@Component
public class IridaRegisteredClientsRepository implements RegisteredClientRepository {

	private final IridaClientDetailsRepository clientDetailsRepository;

	public IridaRegisteredClientsRepository(IridaClientDetailsRepository clientDetailsRepository) {
		this.clientDetailsRepository = clientDetailsRepository;
	}

	@Override
	public void save(RegisteredClient registeredClient) {
		IridaClientDetails entity = toEntity(registeredClient);

		clientDetailsRepository.save(entity);
	}

	@Override
	public RegisteredClient findById(String id) {
		if (clientDetailsRepository.existsById(Long.parseLong(id))) {
			IridaClientDetails client = clientDetailsRepository.findById(Long.parseLong(id)).get();

			return toObject(client);
		} else {
			return null;
		}
	}

	@Override
	public RegisteredClient findByClientId(String clientId) {
		IridaClientDetails client = clientDetailsRepository.loadClientDetailsByClientId(clientId);

		if (client == null) {
			return null;
		} else {
			return toObject(client);
		}
	}

	private IridaClientDetails toEntity(RegisteredClient registeredClient) {
		IridaClientDetails client = new IridaClientDetails();

		client.setId(Long.parseLong(registeredClient.getId()));
		client.setClientId(registeredClient.getClientId());
		client.setClientSecret(registeredClient.getClientSecret());
		client.setRegisteredRedirectUri(registeredClient.getRedirectUris().stream().findFirst().get());
		client.setScope(registeredClient.getScopes());
		client.setAuthorizedGrantTypes(registeredClient.getAuthorizationGrantTypes()
				.stream()
				.map(AuthorizationGrantType::getValue)
				.collect(Collectors.toSet()));
		client.setAccessTokenValiditySeconds(
				(int) registeredClient.getTokenSettings().getAccessTokenTimeToLive().toSeconds());
		client.setRefreshTokenValiditySeconds(
				(int) registeredClient.getTokenSettings().getRefreshTokenTimeToLive().toSeconds());

		return client;
	}

	private RegisteredClient toObject(IridaClientDetails client) {
		RegisteredClient.Builder builder = RegisteredClient.withId(client.getId().toString())
				.clientId(client.getClientId())
				.clientIdIssuedAt(client.getCreatedDate().toInstant())
				.clientSecret(client.getClientSecret())
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
				.authorizationGrantTypes((grantTypes) -> {
					// if refresh token validaty is null disable refresh tokens
					if (client.getRefreshTokenValiditySeconds() == null) {
						grantTypes.removeIf(filter -> filter.equals(AuthorizationGrantType.REFRESH_TOKEN));
					}
					client.getAuthorizedGrantTypes()
							.forEach(grantType -> grantTypes.add(new AuthorizationGrantType(grantType)));
				})
				.scopes((scopes) -> scopes.addAll(client.getScope()))
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.tokenSettings(TokenSettings.builder()
						.accessTokenTimeToLive(Duration.ofSeconds(client.getAccessTokenValiditySeconds()))
						.refreshTokenTimeToLive(Duration.ofSeconds(client.getRefreshTokenValiditySeconds() == null ?
								1L :
								client.getRefreshTokenValiditySeconds()))
						.build());

		String redirectUri = client.getRedirectUri();
		if (redirectUri != null) {
			builder.redirectUri(redirectUri);
		}

		return builder.build();
	}
}

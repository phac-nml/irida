package ca.corefacility.bioinformatics.irida.oauth2;

import java.util.List;

import javax.annotation.Nullable;
import javax.transaction.Transactional;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * A customized version of {@link JdbcOAuth2AuthorizationService} that adds a method to find
 * {@link OAuth2Authorization}s with {@link OAuth2AccessToken}s for a specific {@link RegisteredClient}.
 */
public class IridaOAuth2AuthorizationService extends JdbcOAuth2AuthorizationService {

	private final String QUERY_AUTHORIZATION_BY_REGISTERED_CLIENT_ID = "SELECT * FROM oauth2_authorization WHERE access_token_value IS NOT NULL and registered_client_id = ?";

	public IridaOAuth2AuthorizationService(JdbcOperations jdbcOperations,
			RegisteredClientRepository registeredClientRepository) {
		super(jdbcOperations, registeredClientRepository);
	}

	@Transactional
	public List<OAuth2Authorization> findAccessTokensByRegisteredClientId(String registeredClientId) {
		PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(new Object[] { registeredClientId });
		List<OAuth2Authorization> authorizations = getJdbcOperations()
				.query(QUERY_AUTHORIZATION_BY_REGISTERED_CLIENT_ID, pss, getAuthorizationRowMapper());
		return authorizations;
	}

	@Override
	@Transactional
	public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
		return super.findByToken(token, tokenType);
	}

	@Override
	@Transactional
	public OAuth2Authorization findById(String id) {
		return super.findById(id);
	}

	@Override
	@Transactional
	public void remove(OAuth2Authorization authorization) {
		super.remove(authorization);
	}

	@Override
	@Transactional
	public void save(OAuth2Authorization authorization) {
		super.save(authorization);
	}
}

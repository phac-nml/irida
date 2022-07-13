package ca.corefacility.bioinformatics.irida.oauth2;

import java.util.List;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

public class IridaOAuth2AuthorizationService extends JdbcOAuth2AuthorizationService {

    private final String QUERY_AUTHORIZATION_BY_REGISTERED_CLIENT_ID = "SELECT * FROM oauth2_authorization WHERE access_token_value IS NOT NULL and registered_client_id = ?";

    public IridaOAuth2AuthorizationService(JdbcOperations jdbcOperations,
            RegisteredClientRepository registeredClientRepository) {
        super(jdbcOperations, registeredClientRepository);
    }

    public List<OAuth2Authorization> findAccessTokensByRegisteredClientId(String registeredClientId) {
        PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(new Object[] { registeredClientId });
        List<OAuth2Authorization> authorizations = getJdbcOperations()
                .query(QUERY_AUTHORIZATION_BY_REGISTERED_CLIENT_ID, pss, getAuthorizationRowMapper());
        return authorizations;
    }
}

package ca.corefacility.bioinformatics.irida.oauth2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

/**
 * An {@link} Authentication implementation used for the OAuth 2.0 Resource Owner Password Credentials Grant.
 * 
 * @see OAuth2ResourceOwnerPasswordAuthenticationProvider
 */
public class OAuth2ResourceOwnerPasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

	private final Set<String> scopes;

	/**
	 * Constructs an {@code OAuth2ResourceOwnerPasswordAuthenticationToken} using the provider parameters.
	 * 
	 * @param authorizationGrantType the authorization grant type
	 * @param clientPrincipal        the authenticated client principal
	 * @param scopes                 the requested scope(s)
	 * @param additionalParameters   the additional parameters
	 */
	public OAuth2ResourceOwnerPasswordAuthenticationToken(AuthorizationGrantType authorizationGrantType,
			Authentication clientPrincipal, @Nullable Set<String> scopes,
			@Nullable Map<String, Object> additionalParameters) {
		super(AuthorizationGrantType.PASSWORD, clientPrincipal, additionalParameters);
		this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
	}

	/**
	 * Returns the requested scope(s).
	 *
	 * @return the requested scope(s), or an empty {@code Set} if not available
	 */
	public Set<String> getScopes() {
		return this.scopes;
	}
}

package ca.corefacility.bioinformatics.irida.security.annotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * Security context factory listening for {@link WithMockOAuth2Client} annotations on junit tests. Adds a
 * OAuth2Authentication object into the security context.
 */
public class WithMockOAuth2SecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2Client> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SecurityContext createSecurityContext(WithMockOAuth2Client withClient) {
		// Get the username
		String username = withClient.username();
		if (username == null) {
			throw new IllegalArgumentException("Username cannot be null");
		}

		// Get the user roles
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : withClient.roles()) {
			if (role.startsWith("ROLE_")) {
				throw new IllegalArgumentException("roles cannot start with ROLE_ Got " + role);
			}
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		}

		// Get the client id
		String clientId = withClient.clientId();

		// Create the UsernamePasswordAuthenticationToken
		User principal = new User(username, withClient.password(), true, true, true, true, authorities);

		// Create the JwtAuthenticationToken
		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "none")
				.subject(principal.getUsername())
				.audience(Collections.singletonList(clientId))
				.issuedAt(Instant.MIN)
				.expiresAt(Instant.MAX)
				.issuer("https://issuer.example.org")
				.jti("jti")
				.notBefore(Instant.MIN)
				.build();
		JwtAuthenticationToken oAuth = new JwtAuthenticationToken(jwt, authorities);

		// Add the OAuth2Authentication object to the security context
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(oAuth);
		return context;
	}

}

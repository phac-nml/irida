package ca.corefacility.bioinformatics.irida.security.annotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * Security context factory listening for {@link WithMockOAuth2Client}
 * annotations on junit tests. Adds a OAuth2Authentication object into the
 * security context.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class WithMockOAuth2SecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2Client> {

	@Override
	public SecurityContext createSecurityContext(WithMockOAuth2Client withClient) {
		String username = withClient.username();
		if (username == null) {
			throw new IllegalArgumentException("Username cannot be null");
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : withClient.roles()) {
			if (role.startsWith("ROLE_")) {
				throw new IllegalArgumentException("roles cannot start with ROLE_ Got " + role);
			}
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		}

		String clientId = withClient.clientId();

		User principal = new User(username, withClient.password(), true, true, true, true, authorities);
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
				principal.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		AuthorizationRequest authRequest = mock(AuthorizationRequest.class);
		when(authRequest.getClientId()).thenReturn(clientId);
		OAuth2Authentication oAuth = new OAuth2Authentication(authRequest, authentication);
		context.setAuthentication(oAuth);
		return context;
	}

}

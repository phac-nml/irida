package ca.corefacility.bioinformatics.irida.web.filter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * Anonymous authentication filter that sets anonymous tokens as being
 * unauthenticated
 * 
 *
 */
public class UnauthenticatedAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {

	/**
	 * Creates a filter with a principal named "anonymousUser" and the single
	 * authority "ROLE_ANONYMOUS".
	 *
	 * @param key
	 *            the key to identify tokens created by this filter
	 */
	public UnauthenticatedAnonymousAuthenticationFilter(String key) {
		super(key, "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
	}

	/**
	 * Create a new UnauthenticatedAnonymousAuthenticationFilter with the given
	 * key, principal, and authorities.
	 * 
	 * @param key
	 *            key the key to identify tokens created by this filter
	 * @param principal
	 *            the principal which will be used to represent anonymous users
	 * @param authorities
	 *            the authority list for anonymous users
	 */
	public UnauthenticatedAnonymousAuthenticationFilter(String key, Object principal, List<GrantedAuthority> authorities) {
		super(key, principal, authorities);
	}

	/**
	 * Create the anonymous auth token with Authenticated set to false
	 * 
	 * @param request
	 *            the incoming request
	 */
	@Override
	protected Authentication createAuthentication(HttpServletRequest request) {
		Authentication createAuthentication = super.createAuthentication(request);
		createAuthentication.setAuthenticated(false);
		return createAuthentication;
	}
}

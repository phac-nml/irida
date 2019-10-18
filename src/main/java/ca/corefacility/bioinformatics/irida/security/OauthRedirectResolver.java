package ca.corefacility.bioinformatics.irida.security;

import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;

/**
 * Redirect resolver for Oauth2 clients that allows any url for redirection
 */
// TODO: 10/9/19  Remove this when registered redirect URIs are added to clients
@Deprecated
public class OauthRedirectResolver extends DefaultRedirectResolver {
  /**
   * Resolve the redirect for a client
   *
   * @param requestedRedirect The requested redirect uri for the client
   * @param client            The client authorizing a oauth2 request
   *
   * @return The requestedRedirect if it would throw a InvalidRequestException in the parent method
   * @throws OAuth2Exception if client has no authorized grant types or grant type is not a redirect type
   */
  public String resolveRedirect(String requestedRedirect, ClientDetails client) throws OAuth2Exception {
    try {
      return super.resolveRedirect(requestedRedirect, client);
    }
    // Catch InvalidRequestException which is only thrown when no redirect_uri's are registered with the client.
    catch (InvalidRequestException e) {
      return requestedRedirect;
    }
  }
}

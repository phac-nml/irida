package ca.corefacility.bioinformatics.irida.security;

import java.util.Set;

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;

public class OauthRedirectResolver extends DefaultRedirectResolver {
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

package ca.corefacility.bioinformatics.irida.web.controller.api.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;

/**
 * Produces a more helpful error message than
 * "An Authentication object was not found in the SecurityContext" when a client
 * attempts to connect without a token.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class CustomOAuth2ExceptionTranslator extends DefaultWebResponseExceptionTranslator {
	private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

	@Override
	public ResponseEntity<OAuth2Exception> translate(Exception ex) throws Exception {
		Throwable[] causeChain = throwableAnalyzer.determineCauseChain(ex);
		RuntimeException ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(
				AuthenticationException.class, causeChain);

		// defer to the default behaviour when dealing with anything
		// except for an unauthorized response.
		if (ase == null) {
			return super.translate(ex);
		}

		// handle the unauthorized request.
		OAuth2Exception e = new OAuth2Exception("unauthorized", ex) {
			public String getMessage() {
				return "No client credentials were provided. You must get an access token from the oauth provider at /oauth/token.";
			}
		};

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));

		ResponseEntity<OAuth2Exception> response = new ResponseEntity<OAuth2Exception>(e, headers,
				HttpStatus.valueOf(401));

		return response;
	}
}

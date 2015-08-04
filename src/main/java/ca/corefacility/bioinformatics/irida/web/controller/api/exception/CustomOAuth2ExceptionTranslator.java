package ca.corefacility.bioinformatics.irida.web.controller.api.exception;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;

import com.google.common.collect.ImmutableMap;

/**
 * Produces a more helpful error message than
 * "An Authentication object was not found in the SecurityContext" when a client
 * attempts to connect without a token.
 * 
 *
 */
public class CustomOAuth2ExceptionTranslator extends DefaultWebResponseExceptionTranslator {
	private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2ExceptionTranslator.class);

	/** map of exception types to a human-readble message for the client */
	private static final Map<Class<? extends Exception>, String> EXCEPTION_MESSAGES = ImmutableMap
			.of(AuthenticationException.class,
					"No client credentials were provided. You must get an access token from the oauth provider at /api/oauth/token.",
					InvalidGrantException.class,
					"The credentials you provided were invalid. Please provide valid credentials and try again.");

	private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

	@Override
	public ResponseEntity<OAuth2Exception> translate(Exception ex) throws Exception {
		final Throwable[] causeChain = throwableAnalyzer.determineCauseChain(ex);

		final Optional<Map.Entry<Class<? extends Exception>, String>> exceptionHandled = EXCEPTION_MESSAGES.entrySet()
				.stream().filter(c -> throwableAnalyzer.getFirstThrowableOfType(c.getKey(), causeChain) != null)
				.findAny();

		// defer to the default behaviour when dealing with anything
		// except for an unauthorized response.
		if (!exceptionHandled.isPresent()) {
			logger.debug("Passing exception " + ex + " to super to handle.");
			return super.translate(ex);
		}

		// handle the unauthorized request.
		final OAuth2Exception e = new OAuth2Exception(exceptionHandled.get().getValue(), ex);

		final HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));

		return new ResponseEntity<OAuth2Exception>(e, headers, HttpStatus.valueOf(401));
	}
}
